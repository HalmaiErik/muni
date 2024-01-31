package com.muni.bankaccountdata.request;

import lombok.Data;

@Data
public class AccountFullInfoRequest {

    private String email;
    private String accountExternalId;
    private boolean refresh;
}
