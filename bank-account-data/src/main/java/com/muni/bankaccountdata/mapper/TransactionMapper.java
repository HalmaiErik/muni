package com.muni.bankaccountdata.mapper;

import com.muni.bankaccountdata.db.entity.Account;
import com.muni.bankaccountdata.db.entity.Customer;
import com.muni.bankaccountdata.db.entity.Transaction;
import com.muni.bankaccountdata.dto.gocardless.AccountTransactionDto;
import com.muni.bankaccountdata.dto.internal.TransactionCategoryDto;
import com.muni.bankaccountdata.dto.internal.TransactionDto;
import com.muni.bankaccountdata.dto.util.DeserializerUtil;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TransactionMapper {

    public static Transaction apiDtoToEntity(AccountTransactionDto accountTransactionDto, Customer customer,
                                             Account account, Double conversionRate) {
        return Transaction.builder()
                .externalId(accountTransactionDto.getTransactionId())
                .refFromInstitution(accountTransactionDto.getRefFromInstitution())
                .amount(Double.valueOf(DeserializerUtil.DECIMAL_FORMAT.format(accountTransactionDto.getAmount() * conversionRate)))
                .bookingDate(accountTransactionDto.getBookingDate())
                .remittanceInfo(accountTransactionDto.getRemittanceInfo())
                .customer(customer)
                .account(account)
                .build();
    }

    public static TransactionDto entityToInternalDto(Transaction transaction) {
        Account account = transaction.getAccount();

        return TransactionDto.builder()
                .externalId(transaction.getExternalId())
                .institutionName(account.getInstitutionName())
                .institutionLogo(account.getInstitutionLogo())
                .refFromInstitution(transaction.getRefFromInstitution())
                .accountIban(account.getIban())
                .amount(transaction.getAmount())
                .bookingDate(transaction.getBookingDate())
                .remittanceInfo(transaction.getRemittanceInfo())
                .categories(Optional.ofNullable(transaction.getCategories())
                                .orElseGet(HashSet::new)
                                .stream()
                                .map(category -> TransactionCategoryDto.builder()
                                                    .id(category.getId())
                                                    .name(category.getName())
                                                    .colorCode(category.getColorCode())
                                                    .build())
                                .collect(Collectors.toList()))
                .build();
    }
}
