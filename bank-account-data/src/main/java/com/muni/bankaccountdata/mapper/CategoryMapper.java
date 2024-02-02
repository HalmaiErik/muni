package com.muni.bankaccountdata.mapper;

import com.muni.bankaccountdata.dto.internal.CategoryDto;

import java.util.stream.Collectors;

public class RuleMapper {

    public static Rule dtoToEntity(CategoryDto categoryDto) {
        return Rule.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .colorCode(categoryDto.getColorCode())
                .conditions(categoryDto.getConditions()
                        .stream()
                        .map(ConditionMapper::dtoToEntity)
                        .collect(Collectors.toSet()))
                .build();
    }

    public static CategoryDto entityToDto(Rule rule) {
        return CategoryDto.builder()
                .id(rule.getId())
                .name(rule.getName())
                .colorCode(rule.getColorCode())
                .conditions(rule.getConditions()
                        .stream()
                        .map(ConditionMapper::entityToDto)
                        .collect(Collectors.toSet()))
                .build();
    }
}
