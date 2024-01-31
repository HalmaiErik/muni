package com.muni.bankaccountdata.dto.internal;

import lombok.Data;

@Data
public class ConditionDto {

    private String transactionColumn;
    private String operation;
    private String value;
}
