package com.muni.bankaccountdata.service;

import com.muni.bankaccountdata.db.entity.*;
import com.muni.bankaccountdata.db.entity.enums.Operation;
import com.muni.bankaccountdata.db.repository.ConditionRepository;
import com.muni.bankaccountdata.db.repository.CategoryRepository;
import com.muni.bankaccountdata.db.repository.TransactionRepository;
import com.muni.bankaccountdata.dto.internal.CategoryDto;
import com.muni.bankaccountdata.exception.ApiException;
import com.muni.bankaccountdata.mapper.CategoryMapper;
import com.muni.bankaccountdata.validator.AccountValidator;
import com.muni.bankaccountdata.validator.CategoryValidator;
import com.muni.bankaccountdata.validator.CustomerValidator;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final TransactionRepository transactionRepository;
    private final ConditionRepository conditionRepository;
    private final CategoryRepository categoryRepository;
    private final CustomerValidator customerValidator;
    private final AccountValidator accountValidator;
    private final CategoryValidator categoryValidator;

    public CategoryService(TransactionRepository transactionRepository, ConditionRepository conditionRepository,
                           CategoryRepository categoryRepository, CustomerValidator customerValidator,
                           AccountValidator accountValidator, CategoryValidator categoryValidator) {
        this.transactionRepository = transactionRepository;
        this.conditionRepository = conditionRepository;
        this.categoryRepository = categoryRepository;
        this.customerValidator = customerValidator;
        this.accountValidator = accountValidator;
        this.categoryValidator = categoryValidator;
    }

    public void createCategory(String token, CategoryDto categoryDto) {
        Customer customer = customerValidator.validateAndGetRequiredCustomer(token);

        Category category = categoryRepository.save(CategoryMapper.dtoToEntity(categoryDto, customer));

        List<Condition> conditions = category.getConditions()
                .stream()
                .peek(condition -> condition.setCategory(category))
                .toList();
        conditionRepository.saveAll(conditions);
    }

    public List<CategoryDto> getCustomerCategories(String token) {
        try {
            Customer customer = customerValidator.validateAndGetRequiredCustomer(token);

            List<Category> categories = categoryRepository.getAllByCustomer_Id(customer.getId());

            return categories.stream()
                    .map(CategoryMapper::entityToDto)
                    .collect(Collectors.toList());
        } catch (ApiException e) {
            return new ArrayList<>();
        }
    }

    public void categorizeAccountTransactions(String token, String accountExternalId, Long categoryId) {
        Customer customer = customerValidator.validateAndGetRequiredCustomer(token);
        Account account = accountValidator.getRequiredCustomerAccount(customer, accountExternalId);
        Category category = categoryValidator.getRequiredCustomerCategory(customer, categoryId);

        List<Transaction> transactions = transactionRepository.findAllByAccount_Id(account.getId());
        for (Condition condition : category.getConditions()) {
            String columnName = StringUtils.capitalize(condition.getTransactionColumn().getName().toLowerCase());

            transactions.removeIf(transaction -> transactionRespectsCondition(transaction, condition, columnName));
        }

        transactions.forEach(transaction -> transaction.getCategories().add(category));
        transactionRepository.saveAll(transactions);
    }

    private boolean transactionRespectsCondition(Transaction transaction, Condition condition, String columnName) {
        try {
            Method columnGetter = transaction.getClass().getMethod("get" + columnName);
            String columnValue = columnGetter.invoke(transaction).toString();

            if (Operation.EQUALS.equals(condition.getOperation())) {
                return condition.getValue().equals(columnValue);
            } else if (Operation.CONTAINS.equals(condition.getOperation())) {
                return columnValue.contains(condition.getValue());
            }

            return false;
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new ApiException(e.getMessage());
        }
    }
}
