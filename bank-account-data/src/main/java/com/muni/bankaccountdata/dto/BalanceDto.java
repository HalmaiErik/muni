package com.muni.bankaccountdata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BalanceDto {

    @JsonProperty("balanceAmount")
    private AmountDto amount;

    @JsonProperty("balanceType")
    private String balanceType;

    @JsonProperty("referenceDate")
    private String referenceDate;

    @JsonProperty("creditLimitIncluded")
    private Boolean creditLimitIncluded;
}
