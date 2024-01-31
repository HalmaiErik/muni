package com.muni.bankaccountdata.dto.internal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountDto {

    private String externalId;

    private String name;

    private String iban;

    private String currency;

    private Status status;

    private String institutionName;

    private String institutionLogo;

    private Double balance;

    public enum Status {
        ACTIVE, EXPIRED;
    }
}
