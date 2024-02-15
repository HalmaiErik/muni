package com.muni.bankaccountdata.dto.internal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategorySpentAmountDto {

    private String categoryName;

    private String categoryColorCode;

    private Double spentAmount;
}
