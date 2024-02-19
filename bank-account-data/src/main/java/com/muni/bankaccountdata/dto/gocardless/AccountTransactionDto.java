package com.muni.bankaccountdata.dto.gocardless;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AccountTransactionDto {

    private String transactionId;

    private String refFromInstitution;

    private Double amount;

    private String currency;

    private LocalDate bookingDate;

    private String remittanceInfo;
}
