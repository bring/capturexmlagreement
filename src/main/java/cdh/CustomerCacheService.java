package cdh;

import java.util.List;

public interface CustomerCacheService {

    CustomerModel getCustomer(String customerNumber);

    List<String> getAllCustomerIds();

    List<CustomerModel> getAllCustomers();

    String numberOfCacheHits();

    String numberOfCacheMisses();

    String getLastCacheRefreshTimeStamp();

}
