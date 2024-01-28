package com.muni.bankaccountdata.request;

import lombok.Data;

@Data
public class GetAccountTransactionsRequest {

    private String email;
    private String accountExternalId;
    private boolean refresh;
}
