package cdh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
@Configuration
@EnableScheduling
public class CustomerCacheServiceImpl implements CustomerCacheService, ExchangeRateCacheService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerCacheServiceImpl.class);
    private final AtomicLong cacheHits = new AtomicLong(0);
    private final AtomicLong cacheMisses = new AtomicLong(0);
    
    private final AtomicLong cacheHitsExchangeRate = new AtomicLong(0);
    private final AtomicLong cacheMissesExchangeRate = new AtomicLong(0);
    


//
//    @Autowired
//    ExchangeRateRepository getCurrencyExchangeRate;

    private String lastCacheRefreshTimeStamp;
    private  Map<String, CustomerModel> customerRecordsCache = new ConcurrentHashMap<String, CustomerModel>();
    private Map<String, Map<String, ExchangeRateModel>> exchangeRateMapCache = new ConcurrentHashMap<String, Map<String, ExchangeRateModel>>();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");



    @Override
    public Map<String, ExchangeRateModel> getCurrentDateExchangeCurrencies(String currency) {
    	if(!exchangeRateMapCache.isEmpty()) {
    		cacheHitsExchangeRate.incrementAndGet();
        	return exchangeRateMapCache.get(currency);
    	}
        	else {
        		cacheMissesExchangeRate.incrementAndGet();
        	return Collections.EMPTY_MAP;
        	}
    }
        
    @Override
	public ExchangeRateModel getCurrentDateExchangeRate(String fromCurrency, String toCurrency) {
    	//System.out.println("inside  getCurrentDateExchangeCurrency() ");
		//System.out.println("size of main map =  " + exchangeRateMapCache.size());
			if(!exchangeRateMapCache.isEmpty()) {
			Map<String, ExchangeRateModel> innerMap = exchangeRateMapCache.get(fromCurrency);
			//System.out.println("size of inner map =  " + innerMap.size());
			if(!innerMap.isEmpty()) {
				cacheHitsExchangeRate.incrementAndGet();
				return  innerMap.get(toCurrency);
			}else {
				cacheMissesExchangeRate.incrementAndGet();
				return new ExchangeRateModel();
			}
			}else
				cacheMissesExchangeRate.incrementAndGet();
			return new ExchangeRateModel();
	}
    
    
    @Override
    public CustomerModel getCustomer(String customerNumber) {
        long startTime = System.currentTimeMillis();
        if (!StringUtils.isEmpty(customerNumber)) {
            CustomerModel customerModel = customerRecordsCache.get(customerNumber);
            LOGGER.info("Response time for returning getCustomer [{}ms]", (System.currentTimeMillis() - startTime));
            if (customerModel != null) {
                cacheHits.incrementAndGet();
            } else {
                cacheMisses.incrementAndGet();
                LOGGER.info("Could not find Customer with id [{}] in the cache", customerNumber);
            }
            return customerModel;
        }
        return new CustomerModel();
    }

    @Override
    public List<String> getAllCustomerIds() {
        long startTime = System.currentTimeMillis();
        List<String> stringList = customerRecordsCache.keySet().stream().collect(Collectors.toList());
        LOGGER.info("Response time for returning getAllCustomerIds [{}ms]", (System.currentTimeMillis() - startTime));
        return stringList;
    }

    @Override
    public List<CustomerModel> getAllCustomers() {
        long startTime = System.currentTimeMillis();
        List<CustomerModel> customerModels = customerRecordsCache.values().stream().collect(Collectors.toList());
        LOGGER.info("Response time for returning getAllCustomers [{}ms]", (System.currentTimeMillis() - startTime));
        return customerModels;
    }

    public String numberOfCacheHits() {
        return cacheHits.toString();
    }



    public String numberOfCacheMisses() {
        return cacheMisses.toString();
    }

    public String numberOfExchangeRateCacheMisses() {
        return cacheMissesExchangeRate.toString();
    }

    public String getLastCacheRefreshTimeStamp() {
        return lastCacheRefreshTimeStamp;
    }

    public Map<String, CustomerModel>   refreshCDH() {
        long startTime = System.currentTimeMillis();
        Map<String,CustomerModel> customerMap = new HashMap<String, CustomerModel>();      
        try {
            CDHRepository getCustomerRecordsFromCDH = new CDHRepository();
        	customerMap = getCustomerRecordsFromCDH.invokeApiToFetchCustomerData();  
        } catch (Exception e) {
            LOGGER.error("**FATAL: Error while trying to read records from CDH to populate the cache**");
        }
        long endTime = System.currentTimeMillis();
        if (customerMap != null && !customerMap.isEmpty()) {
            customerRecordsCache.clear();
            customerRecordsCache.putAll(customerMap);

        } else {
            LOGGER.error("***FATAL: Could not fetch customer records from CDH. Cache Will be Empty");
            throw new RuntimeException("Could not populate Customer Cache");
        }
        lastCacheRefreshTimeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        return customerRecordsCache;
    }

	
}
