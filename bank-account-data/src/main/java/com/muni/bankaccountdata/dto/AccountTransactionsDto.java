package com.muni.bankaccountdata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccountTransactionsDto {

    @JsonProperty("transactions")
    private TransactionsDto transactions;
}
