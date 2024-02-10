package com.muni.bankaccountdata.dto.internal;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class CategoryDto {

    private Long id;

    private String name;

    private String colorCode;

    private List<ConditionDto> conditions;
}