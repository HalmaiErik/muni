package com.muni.bankaccountdata.request;

import lombok.Data;

@Data
public class AccountFullInfoRequest {

    private String accountExternalId;
    private boolean refresh;
}
