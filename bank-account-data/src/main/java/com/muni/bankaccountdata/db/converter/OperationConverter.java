package com.muni.bankaccountdata.db.converter;

import com.muni.bankaccountdata.db.entity.enums.Operation;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class OperationConverter implements AttributeConverter<Operation, String> {

    @Override
    public String convertToDatabaseColumn(Operation operation) {
        if (operation == null) {
            return null;
        }

        return operation.getName();
    }

    @Override
    public Operation convertToEntityAttribute(String name) {
        if (name == null) {
            return null;
        }

        return Stream.of(Operation.values())
                .filter(column -> column.getName().equals(name))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
