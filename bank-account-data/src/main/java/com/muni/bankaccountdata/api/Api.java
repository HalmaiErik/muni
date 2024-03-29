package com.muni.bankaccountdata.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;

public interface Api {

    static HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }
}
