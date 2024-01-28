package com.muni.bankaccountdata.dto.internal;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AccountDto {

    private String externalId;

    private String name;

    private String iban;

    private String currency;

    private LocalDate expirationDate;

    private String institutionName;

    private String institutionLogo;
}
