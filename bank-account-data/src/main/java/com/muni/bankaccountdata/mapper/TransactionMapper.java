package com.muni.bankaccountdata.mapper;

import com.muni.bankaccountdata.db.entity.Account;
import com.muni.bankaccountdata.db.entity.Customer;
import com.muni.bankaccountdata.db.entity.Transaction;
import com.muni.bankaccountdata.dto.gocardless.AccountTransactionDto;
import com.muni.bankaccountdata.dto.internal.TransactionDto;
import jakarta.persistence.Column;
import org.springframework.stereotype.Component;

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
                .build();
    }
}
