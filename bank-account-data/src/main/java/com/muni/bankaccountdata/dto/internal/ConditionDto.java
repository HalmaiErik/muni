package com.muni.bankaccountdata.dto.internal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConditionDto {

    private Long id;

    private String transactionColumn;

    private String operation;

    private String value;
}
