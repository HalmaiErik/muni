package com.muni.bankaccountdata.dto.internal;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StatsDto {

    private Double inAmount;

    private Double outAmount;

    List<CategorySpentAmountDto> categorySpentAmounts;
}
