package com.muni.bankaccountdata.mapper;

import com.muni.bankaccountdata.db.entity.Account;
import com.muni.bankaccountdata.db.entity.Category;
import com.muni.bankaccountdata.db.entity.Customer;
import com.muni.bankaccountdata.db.entity.Transaction;
import com.muni.bankaccountdata.dto.gocardless.AccountTransactionDto;
import com.muni.bankaccountdata.dto.internal.TransactionCategoryDto;
import com.muni.bankaccountdata.dto.internal.TransactionDto;
import jakarta.persistence.Column;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TransactionMapper {

    public static Transaction apiDtoToEntity(AccountTransactionDto accountTransactionDto, Customer customer, Account account) {
        return Transaction.builder()
                .externalId(accountTransactionDto.getTransactionId())
                .refFromInstitution(accountTransactionDto.getRefFromInstitution())
                .amount(accountTransactionDto.getAmount())
                .bookingDate(accountTransactionDto.getBookingDate())
                .remittanceInfo(accountTransactionDto.getRemittanceInfo())
                .customer(customer)
                .account(account)
                .build();
    }

    public static TransactionDto entityToInternalDto(Transaction transaction) {
        return TransactionDto.builder()
                .refFromInstitution(transaction.getRefFromInstitution())
                .amount(transaction.getAmount())
                .bookingDate(transaction.getBookingDate())
                .remittanceInfo(transaction.getRemittanceInfo())
                .categories(transaction.getCategories()
                                .stream()
                                .map(category -> TransactionCategoryDto.builder()
                                                    .name(category.getName())
                                                    .colorCode(category.getColorCode())
                                                    .build())
                                .collect(Collectors.toList()))
                .build();
    }
}
