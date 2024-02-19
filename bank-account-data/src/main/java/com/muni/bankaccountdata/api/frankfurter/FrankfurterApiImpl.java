package com.muni.bankaccountdata.api.frankfurter;

import com.muni.bankaccountdata.api.Api;
import com.muni.bankaccountdata.dto.frankfurter.ConversionRateToUsd;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class FrankfurterApiImpl implements FrankfurterApi {

    private final RestTemplate restTemplate;

    public FrankfurterApiImpl() {
        restTemplate = new RestTemplate();
    }

    @Override
    public ResponseEntity<ConversionRateToUsd> getConversionRateToUsd(String currency) {
        System.out.println("Executing");
        HttpHeaders headers = Api.createHttpHeaders();
        HttpEntity entity = new HttpEntity(headers);
        Map<String, String> params = Map.of("from", currency, "to", "USD");

        return restTemplate.exchange(CURRENCY_CONVERSION_URL, HttpMethod.GET, entity, ConversionRateToUsd.class, params);
    }
}
