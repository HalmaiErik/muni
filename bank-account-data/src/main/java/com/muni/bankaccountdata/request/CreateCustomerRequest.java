package com.muni.bankaccountdata.request;

import lombok.Data;

@Data
public class CreateCustomerRequest {

    private String email;
    private String requisitionId;
}
