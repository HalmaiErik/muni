package com.muni.bankaccountdata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionAccountDto {

    @JsonProperty("iban")
    private String iban;
}
