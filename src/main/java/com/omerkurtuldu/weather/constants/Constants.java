package com.omerkurtuldu.weather.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constants {
    public static String API_URL;
    public static String API_KEY;

    public static final String ACCESS_KEY_PARAM = "?access_key=";
    public static final String QUERY_KEY_PARAM = "&query=";



    @Value("${weather-stack.api-key}")
    public void setApiKey(String apiKey) {
        Constants.API_KEY = apiKey;
    }

    @Value("${weather-stack.api-url}")
    public void setApiUrl(String apiUrl) {
        Constants.API_URL = apiUrl;
    }
}
