//package cdh;
//
//import java.io.IOException;
//import java.net.URISyntaxException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.utils.URIBuilder;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.EntityUtils;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import groovyjarjarcommonscli.ParseException;
//import no.bring.priceengine.pricecalculator.cdh.rest.oauth.AccessTokenAdapter;
//
//
//
//@Component
//public class ExchangeRateRepository {
//    private static ObjectMapper objectMapper = new ObjectMapper();
//    private final Logger LOGGER = LoggerFactory.getLogger(CDHRepository.class);
//    @Autowired
//    AccessTokenAdapter accessTokenAdapter;
//    private HttpClient httpClient = HttpClientBuilder.create().build();
//    @Value("${oebs.exchangerate.provider.url}")
//    private String host;
//    @Value("${oebs.exchangerate.resource.uri}")
//    private String exchangeRateResource;
//
//    public Map<String, Map<String, ExchangeRateModel>> invokeApiToFetchCurrencyExchangeRateData(String date) throws ParseException, org.json.simple.parser.ParseException, URISyntaxException{
//        long startTime = System.currentTimeMillis();
//        HttpResponse httpResponse = null;
//        String jsonStrOfCurrencyExchangeRate = "";
//        Map<String, Map<String, ExchangeRateModel>> exchangeRateMap = new HashMap<String, Map<String, ExchangeRateModel>>();
//
//        URIBuilder builder =  new URIBuilder(host+exchangeRateResource);
//        builder.setParameter("Conversion_date",date);
//        builder.setParameter("CONVERSION_TYPE","CORPORATE");
//
//        HttpGet httpGet = new HttpGet(builder.build());
//        httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded");
//        httpGet.setHeader("Authorization",String.format("Bearer %s", accessTokenAdapter.getCachedAccessToken()));
//
//        try {
//            LOGGER.debug("Data from EXCHANGE RATE API URI received in {}ms", (System.currentTimeMillis() - startTime));
//            httpResponse = httpClient.execute(httpGet);
//			int status = httpResponse.getStatusLine().getStatusCode();
//            if (status == HttpStatus.SC_OK && httpResponse.getEntity() != null) {
//				jsonStrOfCurrencyExchangeRate = EntityUtils.toString(httpResponse.getEntity());
//
//			JSONParser parser = new JSONParser();
//				JSONObject object = (JSONObject) parser.parse(jsonStrOfCurrencyExchangeRate);
//				JSONArray array = (JSONArray) object.get("items");
//				for(int i=0; i < array.size(); i++){
//					JSONObject exchangeRateObject =  (JSONObject)array.get(i);
//						exchangeRateObject = organizeJsonObject(exchangeRateObject);
//						ExchangeRateModel exchangeRateModel = new ObjectMapper().readValue(exchangeRateObject.toString(), ExchangeRateModel.class);
//						if(exchangeRateMap.containsKey(exchangeRateModel.getFromCurrency())) {
//							exchangeRateMap.get(exchangeRateModel.getFromCurrency()).put(exchangeRateModel.getToCurrency(), exchangeRateModel);
//						}else {
//							Map<String, ExchangeRateModel> innerMap = new HashMap<String, ExchangeRateModel>();
//									innerMap.put(exchangeRateModel.getToCurrency(), exchangeRateModel);
//									exchangeRateMap.put(exchangeRateModel.getFromCurrency(),innerMap);
//						}
//				}
//                return exchangeRateMap;
//            }
//        } catch (IOException ioe) {
//            LOGGER.error("FATAL: Error [{}] while invoking CDH EXCHANGE RATE URI[{}] to populate Cache", ioe.getMessage(), getUrl());
//            throw new RuntimeException("FATAL: Error [" + ioe.getMessage() + "] while invoking CDH API EXCHANGE RATE URI[" + getUrl() + "] to populate Cache");
//        }
//        return Collections.EMPTY_MAP;
//    }
//
//    private String getUrl() {
//        return host.concat(exchangeRateResource);
//    }
//
//    private JSONObject organizeJsonObject(JSONObject customerObject) {
//    	JSONObject finalObject = new JSONObject();
//    	finalObject.put("conversionDate", customerObject.get("conversion_date"));
//    	finalObject.put("fromCurrency", customerObject.get("from_currency"));
//    	finalObject.put("toCurrency", customerObject.get("to_currency"));
//    	finalObject.put("conversionRate", customerObject.get("conversion_rate"));
//
//    	return finalObject;
//    }
//
//}
