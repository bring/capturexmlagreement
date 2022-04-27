package oauth;

import java.time.LocalDateTime;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Component
public class CDHAccessTokenAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CDHAccessTokenAdapter.class);
    private static final int tokenTimeToLiveinMinutes = 50;  //Nomrally a token is valid for 1 hour. check with OeBS Team
    private static String token; //This static variable is the cache :)
    private static String tokenTimeStamp; // Time stamp the token was generated to calculated expiry of the token

    //e.g. https://oauth.api.posten.no/oauth/v2/token
    @Value("${oebs.cdh.token.provider.url}")
    private String host;

    //e.g. /oauth/v2/token
    @Value("${oebs.cdh.token.resource.uri}")
    private String tokenResource;

    //e.g. gy8zgEkoEucMKFLSb89TWkbtZLpA6Sx1
    @Value("${oebs.cdh.token.clientId}")
    private String clientId;

    @Value("${oebs.cdh.token.clientPassword}")
    private String clientPassword;

    @Value("${oebs.token.connecttimeout}")
    private Integer connecttimeout;

    @Value("${oebs.token.readtimeout}")
    private Integer readtimeout;

    public String getCachedAccessToken() {
        if (StringUtils.isEmpty(token) || !isTokenStillValid()) {
            getAndCacheNewOAuthToken();
        }
        return token;
    }

    private void getAndCacheNewOAuthToken() {
        final String url = host + tokenResource;						
        MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("grant_type", "client_credentials");
        RestTemplate restTemplate = new RestTemplate(getHttpComponentsClientHttpRequestFactory());
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(bodyMap,
                constructHttpHeaders());
        ResponseEntity<AccessToken> response = restTemplate.exchange(url, HttpMethod.POST, request, AccessToken.class);
        AccessToken accessToken = response.getBody();
        LOGGER.info("Received New Access Token from OeBS/CDH");
        token = accessToken.getAccess_token();
        tokenTimeStamp =  LocalDateTime.now().toString();
    }

    private HttpComponentsClientHttpRequestFactory getHttpComponentsClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory;
        httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpComponentsClientHttpRequestFactory.setConnectTimeout(connecttimeout);
        httpComponentsClientHttpRequestFactory.setReadTimeout(readtimeout);
        return httpComponentsClientHttpRequestFactory;
    }

    private boolean isTokenStillValid() {
        if (StringUtils.isEmpty(tokenTimeStamp)) {
            return false;
        }
        LocalDateTime timeStamp = LocalDateTime.parse(tokenTimeStamp);
        if (timeStamp.isBefore(LocalDateTime.now().minusMinutes(tokenTimeToLiveinMinutes))) {
            LOGGER.info("Access token [generated: {}] is now expired - generating new token..", tokenTimeStamp);
            return false;
        }
        return true;
    }

    private HttpHeaders constructHttpHeaders() {
        //the clientid and password for requesting a new token must be encoded and set on the headers before requesting token
        final String plainCreds = clientId + ":" + clientPassword;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        headers.setContentType(MediaType.valueOf("application/x-www-form-urlencoded"));
        return headers;
    }

}
