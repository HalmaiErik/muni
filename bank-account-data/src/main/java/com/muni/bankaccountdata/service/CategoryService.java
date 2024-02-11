package com.muni.bankaccountdata.service;

import com.muni.bankaccountdata.db.entity.*;
import com.muni.bankaccountdata.db.entity.enums.Operation;
import com.muni.bankaccountdata.db.entity.enums.Separator;
import com.muni.bankaccountdata.db.repository.ConditionRepository;
import com.muni.bankaccountdata.db.repository.CategoryRepository;
import com.muni.bankaccountdata.db.repository.TransactionRepository;
import com.muni.bankaccountdata.dto.internal.CategoryDto;
import com.muni.bankaccountdata.exception.ApiException;
import com.muni.bankaccountdata.mapper.CategoryMapper;
import com.muni.bankaccountdata.validator.AccountValidator;
import com.muni.bankaccountdata.validator.CategoryValidator;
import com.muni.bankaccountdata.validator.CustomerValidator;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
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

        Category requestCategory = CategoryMapper.dtoToEntity(categoryDto, customer);
        Category savedCategory = categoryRepository.save(requestCategory);

        Set<Condition> requestConditions = new LinkedHashSet<>(requestCategory.getConditions());

        List<Condition> conditions = requestConditions.stream()
                .peek(condition -> condition.setCategory(savedCategory))
                .toList();
        conditionRepository.saveAll(conditions);

        List<Condition> conditionsToDelete = savedCategory.getConditions().stream()
                .filter(condition -> !requestConditions.contains(condition))
                .toList();
        conditionRepository.deleteAll(conditionsToDelete);
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

    public List<CategoryDto> getCustomerCategories(Customer customer) {
        try {
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

        List<Transaction> accountTransactions = transactionRepository.findAllByAccount_Id(account.getId());
        List<List<Transaction>> orSeparatedGroups = new ArrayList<>();

        List<Transaction> currentGroup = new ArrayList<>(accountTransactions);
        orSeparatedGroups.add(currentGroup);

        for (int i = 0; i < category.getConditions().size(); i++) {
            Condition condition = category.getConditions().get(i);
            if (Separator.OR.equals(condition.getSeparator()) && i != 0) {
                currentGroup = new ArrayList<>(accountTransactions);
                orSeparatedGroups.add(currentGroup);
            }

            String columnGetterName = "get" + condition.getTransactionColumn().getName();
            currentGroup.removeIf(transaction -> !transactionRespectsCondition(transaction, condition, columnGetterName));
        }

        List<Transaction> categoryTransactions = orSeparatedGroups.stream()
                .flatMap(Collection::stream)
                .distinct()
                .toList();

        categoryTransactions.forEach(transaction -> addCategoryToTransaction(transaction, category));
        transactionRepository.saveAll(categoryTransactions);
    }

    private boolean transactionRespectsCondition(Transaction transaction, Condition condition, String columnGetterName) {
        try {
            Method columnGetter = transaction.getClass().getMethod(columnGetterName);
            Object column = columnGetter.invoke(transaction);

            if (column != null) {
                String columnValue = column.toString();

                if (Operation.EQUALS.equals(condition.getOperation())) {
                    return condition.getValue().equals(columnValue);
                }
                else if (Operation.CONTAINS.equals(condition.getOperation())) {
                    return columnValue.contains(condition.getValue());
                }
            }

            return false;
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new ApiException(e.getMessage());
        }
    }

    private void addCategoryToTransaction(Transaction transaction, Category category) {
        Set<Category> categories = Optional.ofNullable(transaction.getCategories())
                .orElseGet(HashSet::new);

        categories.add(category);
        transaction.setCategories(categories);
    }
}
