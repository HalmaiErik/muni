package com.muni.bankaccountdata.db.converter;

import com.muni.bankaccountdata.db.entity.enums.Separator;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SeparatorConverter implements AttributeConverter<Separator, String> {

    @Override
    public String convertToDatabaseColumn(Separator separator) {
        if (separator == null) {
            return null;
        }

        return separator.toString();
    }

    @Override
    public Separator convertToEntityAttribute(String name) {
        if (name == null) {
            return null;
        }

        return Separator.valueOf(name);
    }
}
