package com.muni.bankaccountdata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TransactionDto {

    @JsonProperty("transactionId")
    private String id;

    @JsonProperty("debtorName")
    private String debtorName;

    @JsonProperty("debtorAccount")
    private TransactionAccountDto debtorAccount;

    @JsonProperty("creditorName")
    private String creditorName;

    @JsonProperty("creditorAccount")
    private TransactionAccountDto creditorAccount;

    @JsonProperty("transactionAmount")
    private AmountDto amount;

    @JsonProperty("bookingDate")
    private String bookingDate;

    @JsonProperty("remittanceInformationUnstructured")
    private String remittanceInfo;
}
