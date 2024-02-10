package com.muni.bankaccountdata.dto.internal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionCategoryDto {

    private String name;

    private String colorCode;
}
