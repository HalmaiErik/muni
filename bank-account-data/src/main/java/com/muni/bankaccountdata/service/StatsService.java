package com.muni.bankaccountdata.service;

import com.muni.bankaccountdata.db.entity.Account;
import com.muni.bankaccountdata.db.entity.Category;
import com.muni.bankaccountdata.db.entity.Customer;
import com.muni.bankaccountdata.db.entity.Transaction;
import com.muni.bankaccountdata.db.repository.TransactionRepository;
import com.muni.bankaccountdata.dto.internal.CategorySpentAmountDto;
import com.muni.bankaccountdata.dto.internal.StatsDto;
import com.muni.bankaccountdata.validator.AccountValidator;
import com.muni.bankaccountdata.validator.CustomerValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StatsService {

    public static final String OTHERS = "Others";
    public static final String GRAY_COLOR_CODE = "rgb(128, 128, 128)";

    private final TransactionRepository transactionRepository;
    private final CustomerValidator customerValidator;
    private final AccountValidator accountValidator;

    public StatsService(TransactionRepository transactionRepository, CustomerValidator customerValidator,
                        AccountValidator accountValidator) {
        this.transactionRepository = transactionRepository;
        this.customerValidator = customerValidator;
        this.accountValidator = accountValidator;
    }

    public StatsDto getCustomerStats(String token, LocalDate from, LocalDate to) {
        Customer customer = customerValidator.validateAndGetRequiredCustomer(token);

        List<Transaction> transactions = transactionRepository
                .findAllByCustomer_IdAndBookingDateAfterAndBookingDateBefore(customer.getId(), from, to.plusDays(1));

        return calculateTransactionsStats(transactions);
    }

    public StatsDto getAccountStatsBetweenDates(String token, String accountExternalId, LocalDate from, LocalDate to) {
        Customer customer = customerValidator.validateAndGetRequiredCustomer(token);
        Account account = accountValidator.getRequiredCustomerAccount(customer, accountExternalId);

        List<Transaction> transactions = transactionRepository
                .findAllByAccount_IdAndBookingDateAfterAndBookingDateBefore(account.getId(), from, to.plusDays(1));

        return calculateTransactionsStats(transactions);
    }

    private StatsDto calculateTransactionsStats(List<Transaction> transactions) {
        double inAmount = 0;
        double outAmount = 0;
        double nonCategorizedAmount = 0;
        Map<Category, Double> categoryToSpentAmount = new HashMap<>();
        for (Transaction transaction : transactions) {
            double amount = transaction.getAmount();

            if (amount > 0) {
                inAmount += amount;
            }
            else {
                outAmount += amount;
                Set<Category> categories = transaction.getCategories();

                if (categories == null || categories.isEmpty()) {
                    nonCategorizedAmount += transaction.getAmount();
                }
                else {
                    categories.forEach(category -> categoryToSpentAmount.put(category,
                            categoryToSpentAmount.getOrDefault(category, 0.0) + amount));
                }
            }
        }

        List<CategorySpentAmountDto> categorySpentAmounts = categoryToSpentAmount.keySet()
                .stream()
                .map(category -> CategorySpentAmountDto.builder()
                        .categoryName(category.getName())
                        .categoryColorCode(category.getColorCode())
                        .spentAmount(Math.abs(categoryToSpentAmount.get(category)))
                        .build())
                .collect(Collectors.toList());
        categorySpentAmounts.add(CategorySpentAmountDto.builder()
                .categoryName(OTHERS)
                .categoryColorCode(GRAY_COLOR_CODE)
                .spentAmount(Math.abs(nonCategorizedAmount))
                .build());

        return StatsDto.builder()
                .inAmount(inAmount)
                .outAmount(Math.abs(outAmount))
                .categorySpentAmounts(categorySpentAmounts)
                .build();
    }
}
