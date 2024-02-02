package com.muni.bankaccountdata.service;

import com.muni.bankaccountdata.db.entity.Customer;
import com.muni.bankaccountdata.db.entity.Rule;
import com.muni.bankaccountdata.db.entity.enums.Operation;
import com.muni.bankaccountdata.db.repository.ConditionRepository;
import com.muni.bankaccountdata.db.repository.CustomerRepository;
import com.muni.bankaccountdata.db.repository.RuleRepository;
import com.muni.bankaccountdata.db.repository.TransactionRepository;
import com.muni.bankaccountdata.dto.internal.ConditionDto;
import com.muni.bankaccountdata.dto.internal.RuleDto;
import com.muni.bankaccountdata.exception.ApiException;
import com.muni.bankaccountdata.mapper.RuleMapper;
import com.muni.bankaccountdata.validator.CustomerValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RuleService {

    private final TransactionRepository transactionRepository;
    private final ConditionRepository conditionRepository;
    private final RuleRepository ruleRepository;
    private final CustomerValidator customerValidator;

    public RuleService(TransactionRepository transactionRepository, ConditionRepository conditionRepository,
                       RuleRepository ruleRepository, CustomerValidator customerValidator) {
        this.transactionRepository = transactionRepository;
        this.customerValidator = customerValidator;
        this.conditionRepository = conditionRepository;
        this.ruleRepository = ruleRepository;
    }

    public void saveRule(String email, RuleDto ruleDto) {
        Customer customer = customerValidator.getRequiredCustomer(email);

        Rule rule = RuleMapper.dtoToEntity(ruleDto);
        ruleRepository.save(rule);
    }

    public List<RuleDto> getCustomerRules(String email) {
        try {
            Customer customer = customerValidator.getRequiredCustomer(email);

            List<Rule> rules = ruleRepository.getAllByCustomer_Id(customer.getId());

            return rules.stream()
                    .map(RuleMapper::entityToDto)
                    .collect(Collectors.toList());
        }
        catch (ApiException e) {
            return new ArrayList<>();
        }
    }
}
