package com.muni.bankaccountdata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AmountDto {

    @JsonProperty("currency")
    private String currencyCode;

    @JsonProperty("amount")
    private Double amount;
}
