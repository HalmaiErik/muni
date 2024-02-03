package com.muni.bankaccountdata.mapper;

import com.muni.bankaccountdata.db.entity.Category;
import com.muni.bankaccountdata.db.entity.Customer;
import com.muni.bankaccountdata.dto.internal.CategoryDto;

import java.util.stream.Collectors;

public class CategoryMapper {

    public static Category dtoToEntity(CategoryDto categoryDto, Customer customer) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .colorCode(categoryDto.getColorCode())
                .customer(customer)
                .conditions(categoryDto.getConditions()
                        .stream()
                        .map(ConditionMapper::dtoToEntity)
                        .collect(Collectors.toSet()))
                .build();
    }

    public static CategoryDto entityToDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .colorCode(category.getColorCode())
                .conditions(category.getConditions()
                        .stream()
                        .map(ConditionMapper::entityToDto)
                        .collect(Collectors.toSet()))
                .build();
    }
}
