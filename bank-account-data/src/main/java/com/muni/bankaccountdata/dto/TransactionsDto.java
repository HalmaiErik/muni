package com.muni.bankaccountdata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TransactionsDto {

    @JsonProperty("booked")
    private List<TransactionDto> bookedTransactions;

    @JsonProperty("pending")
    private List<TransactionDto> pendingTransactions;
}
