package com.muni.bankaccountdata.dto.internal;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class TransactionDto {

    private String externalId;

    private String institutionName;

    private String institutionLogo;

    private String refFromInstitution;

    private String accountIban;

    private String debtorName;

    private String debtorAccount;

    private String creditorName;

    private String creditorAccount;

    private Double amount;

    private LocalDate bookingDate;

    private String remittanceInfo;

    private List<TransactionCategoryDto> categories;
}
