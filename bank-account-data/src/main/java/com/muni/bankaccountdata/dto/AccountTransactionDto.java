package com.muni.bankaccountdata.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AccountTransactionDto {

    private String transactionId;

    private String debtorName;

    private String debtorAccount;

    private String creditorName;

    private String creditorAccount;

    private Double amount;

    private LocalDate bookingDate;

    private String remittanceInfo;
}
