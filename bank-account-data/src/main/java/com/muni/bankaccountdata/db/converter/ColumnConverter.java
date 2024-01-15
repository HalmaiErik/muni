package com.muni.bankaccountdata.db.converter;

import com.muni.bankaccountdata.db.entity.enums.TransactionColumn;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class ColumnConverter implements AttributeConverter<TransactionColumn, String> {

    @Override
    public String convertToDatabaseColumn(TransactionColumn transactionColumn) {
        if (transactionColumn == null) {
            return null;
        }

        return transactionColumn.getName();
    }

    @Override
    public TransactionColumn convertToEntityAttribute(String name) {
        if (name == null) {
            return null;
        }

        return Stream.of(TransactionColumn.values())
                .filter(transactionColumn -> transactionColumn.getName().equals(name))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
