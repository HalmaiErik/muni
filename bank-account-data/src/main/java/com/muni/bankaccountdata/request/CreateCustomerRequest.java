package com.muni.bankaccountdata.request;

import lombok.Data;

@Data
public class CreateCustomerRequest {

    private String requisitionId;
    private String institutionName;
    private String institutionLogo;
}
