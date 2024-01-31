package com.muni.bankaccountdata.validator;

import com.muni.bankaccountdata.db.entity.Customer;
import com.muni.bankaccountdata.db.repository.CustomerRepository;
import com.muni.bankaccountdata.exception.ApiException;
import org.springframework.stereotype.Component;

@Component
public class CustomerValidator {

    private static final String REQUIRED_CUSTOMER_EXCEPTION_MSG = "No customer found with email %s";

    private final CustomerRepository customerRepository;

    public CustomerValidator(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer getRequiredCustomer(String email) {
        return customerRepository.findCustomerByEmail(email)
                .orElseThrow(() -> new ApiException(String.format(REQUIRED_CUSTOMER_EXCEPTION_MSG, email)));
    }
}
