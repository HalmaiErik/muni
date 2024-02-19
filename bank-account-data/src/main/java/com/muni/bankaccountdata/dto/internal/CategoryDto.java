package com.muni.bankaccountdata.dto.internal;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryDto {

    private Long id;

    private String name;

    private String colorCode;

    private List<ConditionDto> conditions;
}
