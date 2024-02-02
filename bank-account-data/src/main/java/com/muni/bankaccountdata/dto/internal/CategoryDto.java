package com.muni.bankaccountdata.dto.internal;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class RuleDto {

    private Long id;

    private String name;

    private String colorCode;

    private Set<ConditionDto> conditions;
}
