package cdh;

import cdh.rest.oauth.AccessTokenAdapter;
import org.apache.http.ParseException;
import org.springframework.stereotype.Component;


import com.fasterxml.jackson.databind.ObjectMapper;
import cdh.rest.oauth.CDHAccessTokenAdapter;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CDHRepository {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private final Logger LOGGER = LoggerFactory.getLogger(CDHRepository.class);
    private static final String SE = "000771";
    private static final String FI = "101480";
    private static final String DK = "101470";
    private static final String NO = "101471";
    private AccessTokenAdapter accessTokenAdapter = new AccessTokenAdapter();

    private HttpClient httpClient = HttpClientBuilder.create().build();
   // PRODUCTION
    private String host = "https://oebsws.posten.no";
    private String cdhCustomerResource = "/ordserp/ords_cdh/customer_cdh/cdhpe/?orig_system=IMI_ORDER";


    // UAT
//    private String host = "https://uatoebsws.posten.no";
//    private String cdhCustomerResource = "/ordserp/ords_cdh/customer_cdh/cdhpe/?orig_system=IMI_ORDER";

    public Map<String,CustomerModel> invokeApiToFetchCustomerData() throws ParseException,  URISyntaxException{
        long startTime = System.currentTimeMillis();
        HttpResponse httpResponse = null;
        String jsonStrOfCustomersFromCDH = "";
        Map<String,CustomerModel> customerMap = new HashMap<String, CustomerModel>();
        URIBuilder builder =  new URIBuilder(host+cdhCustomerResource);
        HttpGet httpGet = new HttpGet(builder.build());
        httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpGet.setHeader("Authorization",String.format("Bearer %s", accessTokenAdapter.getCachedAccessToken()));

        try {
            LOGGER.debug("Data from CDH API URI received in {}ms", (System.currentTimeMillis() - startTime));
            httpResponse = httpClient.execute(getHttpGETHeader());
			int status = httpResponse.getStatusLine().getStatusCode();
			LOGGER.debug("API Response  - ", status);

            httpResponse = httpClient.execute(httpGet);
            if (status == HttpStatus.SC_OK && httpResponse.getEntity() != null) {
				jsonStrOfCustomersFromCDH = EntityUtils.toString(httpResponse.getEntity());

                JSONParser parser = new org.json.simple.parser.JSONParser();
                JSONObject object = (JSONObject) parser.parse(jsonStrOfCustomersFromCDH);
				JSONArray array = (JSONArray) object.get("items");				
				for(int i=0; i < array.size(); i++){
					JSONObject customerObject =  (JSONObject)array.get(i);
					customerObject = organizeJsonObject(customerObject); 
					CustomerModel customerModel = new ObjectMapper().readValue(customerObject.toString(), CustomerModel.class);
					customerMap.put(customerModel.getCustomerNumber(), customerModel);
				}
                return customerMap;
            }
        } catch (IOException | org.json.simple.parser.ParseException ioe) {
            LOGGER.error("FATAL: Error [{}] while invoking CDH API URI[{}] to populate Cache", ioe.getMessage(), getUrl());
            throw new RuntimeException("FATAL: Error [" + ioe.getMessage() + "] while invoking CDH API URI[" + getUrl() + "] to populate Cache");
        } catch(Exception e){
            e.printStackTrace();
        }
        return Collections.EMPTY_MAP;
    }
    
    private JSONObject organizeJsonObject(JSONObject customerObject) {
    	JSONObject finalObject = new JSONObject();
    	finalObject.put("rownum", customerObject.get("rownum"));
    	finalObject.put("organizationNumber", customerObject.get("organizationnumber"));
    	finalObject.put("customerNumber", customerObject.get("customernumber"));
    	if(DK.equals(customerObject.get("oushortname")))
    		finalObject.put("branchOrFilial", "DK");
    	else if(FI.equals(customerObject.get("oushortname")))
    		finalObject.put("branchOrFilial", "FI");
    	else if(SE.equals(customerObject.get("oushortname")))
    		finalObject.put("branchOrFilial", "SE");
    	else if(NO.equals(customerObject.get("oushortname")))
    		finalObject.put("branchOrFilial", "NO");
    	finalObject.put("branchOrFilialId", customerObject.get("oushortname"));
    	finalObject.put("customerName", customerObject.get("customername"));
    	finalObject.put("accountStatus", customerObject.get("status"));
    	
    	return finalObject;
    }

    private HttpGet getHttpGETHeader() {
        HttpGet httpGet = new HttpGet(getUrl());
        try {
        //Access Token sent in Header as Bearer Token
        Header tokenHeader = new BasicHeader("Authorization", "Bearer " + accessTokenAdapter.getCachedAccessToken());
        httpGet.setHeader("Content-type", "application/x-www-form-urlencoded");
        httpGet.addHeader(tokenHeader);
        return httpGet;
        }catch(Exception e) {
        	e.printStackTrace();
        	return null;
        }
    }

    private String getUrl() {
        return host.concat(cdhCustomerResource);
    }
	
}
