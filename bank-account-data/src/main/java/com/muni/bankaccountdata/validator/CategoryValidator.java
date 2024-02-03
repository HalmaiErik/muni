package com.muni.bankaccountdata.validator;

import com.muni.bankaccountdata.db.entity.Category;
import com.muni.bankaccountdata.db.entity.Customer;
import com.muni.bankaccountdata.db.repository.CategoryRepository;
import com.muni.bankaccountdata.exception.ApiException;
import org.springframework.stereotype.Component;

@Component
public class CategoryValidator {

    private static final String REQUIRED_CUSTOMER_CATEGORY_EXCEPTION_MSG = "No category found with id %s and customer email %s";

    private final CategoryRepository categoryRepository;

    public CategoryValidator(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category getRequiredCustomerCategory(Customer customer, Long categoryId) {
        return categoryRepository.findCategoryByIdAndCustomer_Id(categoryId, customer.getId())
                .orElseThrow(() -> new ApiException(String.format(REQUIRED_CUSTOMER_CATEGORY_EXCEPTION_MSG,
                        categoryId, customer.getEmail())));
    }
}
