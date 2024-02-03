package com.muni.bankaccountdata.request;

import lombok.Data;

@Data
public class CategorizeAccountTransactionsRequest {

    private String accountExternalId;
    private Long categoryId;
}
