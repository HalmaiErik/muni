package com.muni.bankaccountdata.validator;

import com.muni.bankaccountdata.db.entity.Customer;
import com.muni.bankaccountdata.db.entity.Transaction;
import com.muni.bankaccountdata.db.repository.TransactionRepository;
import com.muni.bankaccountdata.exception.ApiException;
import org.springframework.stereotype.Component;

@Component
public class TransactionValidator {

    private static final String REQUIRED_CUSTOMER_TRANSACTION_EXCEPTION_MSG = "No transaction found with externalId %s and customer email %s";

    private final TransactionRepository transactionRepository;

    public TransactionValidator(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction getRequiredCustomerTransaction(Customer customer, String transactionExternalId) {
        return transactionRepository.findTransactionByExternalIdAndCustomer_Id(transactionExternalId, customer.getId())
                .orElseThrow(() -> new ApiException(String.format(REQUIRED_CUSTOMER_TRANSACTION_EXCEPTION_MSG,
                        transactionExternalId, customer.getEmail())));
    }
}
