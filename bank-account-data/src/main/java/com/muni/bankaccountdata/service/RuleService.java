package com.muni.bankaccountdata.service;

import com.muni.bankaccountdata.db.repository.CustomerRepository;
import com.muni.bankaccountdata.db.repository.TransactionRepository;
import com.muni.bankaccountdata.dto.internal.ConditionDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleService {

    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    public RuleService(CustomerRepository customerRepository, TransactionRepository transactionRepository) {
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
    }

    public void createRule(String email, String name, List<ConditionDto> conditions) {

    }
}
