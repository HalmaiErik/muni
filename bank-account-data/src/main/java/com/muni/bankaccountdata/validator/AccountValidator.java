package com.muni.bankaccountdata.validator;

import com.muni.bankaccountdata.db.entity.Account;
import com.muni.bankaccountdata.db.entity.Customer;
import com.muni.bankaccountdata.db.repository.AccountRepository;
import com.muni.bankaccountdata.exception.ApiException;
import org.springframework.stereotype.Component;

@Component
public class AccountValidator {

    private static final String REQUIRED_CUSTOMER_ACCOUNT_EXCEPTION_MSG = "No account found with externalId %s and customer email %s";

    private final AccountRepository accountRepository;

    public AccountValidator(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account getRequiredCustomerAccount(Customer customer, String accountExternalId) {
        return accountRepository.findAccountByExternalIdAndCustomer_Id(accountExternalId, customer.getId())
                .orElseThrow(() -> new ApiException(String.format(REQUIRED_CUSTOMER_ACCOUNT_EXCEPTION_MSG,
                        accountExternalId, customer.getEmail())));
    }
}
