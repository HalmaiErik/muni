package com.muni.bankaccountdata.api.frankfurter;

import com.muni.bankaccountdata.api.Api;
import com.muni.bankaccountdata.dto.frankfurter.ConversionRateToUsd;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

public interface FrankfurterApi extends Api {

    String API_BASE_URL = "https://api.frankfurter.app/latest";
    String CURRENCY_CONVERSION_URL = UriComponentsBuilder.fromHttpUrl(API_BASE_URL)
            .queryParam("from", "{from}")
            .queryParam("to", "{to}")
            .encode()
            .toUriString();

    ResponseEntity<ConversionRateToUsd> getConversionRateToUsd(String currency);
}
