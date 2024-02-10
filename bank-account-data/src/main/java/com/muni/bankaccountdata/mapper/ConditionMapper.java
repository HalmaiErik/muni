package com.muni.bankaccountdata.mapper;

import com.muni.bankaccountdata.db.converter.ColumnConverter;
import com.muni.bankaccountdata.db.converter.OperationConverter;
import com.muni.bankaccountdata.db.entity.Condition;
import com.muni.bankaccountdata.db.entity.enums.Operation;
import com.muni.bankaccountdata.db.entity.enums.TransactionColumn;
import com.muni.bankaccountdata.dto.internal.ConditionDto;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ConditionMapper {

    public static Condition dtoToEntity(ConditionDto conditionDto) {
        return Condition.builder()
                .id(conditionDto.getId())
                .transactionColumn(TransactionColumn.valueOf(conditionDto.getTransactionColumn()))
                .operation(Operation.valueOf(conditionDto.getOperation()))
                .value(conditionDto.getValue())
                .build();
    }

    public static ConditionDto entityToDto(Condition condition) {
        return ConditionDto.builder()
                .id(condition.getId())
                .transactionColumn(condition.getTransactionColumn().toString())
                .operation(condition.getOperation().toString())
                .value(condition.getValue())
                .build();
    }
}
