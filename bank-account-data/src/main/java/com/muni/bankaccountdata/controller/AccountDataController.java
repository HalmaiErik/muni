package com.muni.bankaccountdata.controller;

import com.muni.bankaccountdata.dto.internal.AccountDto;
import com.muni.bankaccountdata.dto.internal.AccountFullInfoDto;
import com.muni.bankaccountdata.dto.internal.CategoryDto;
import com.muni.bankaccountdata.dto.shared.InstitutionDto;
import com.muni.bankaccountdata.dto.shared.RequisitionDto;
import com.muni.bankaccountdata.request.*;
import com.muni.bankaccountdata.service.AccountDataService;
import com.muni.bankaccountdata.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bankaccount")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AccountDataController {

    private static final int BEARER_PREFIX_LENGTH = 7;

    private final AccountDataService accountDataService;
    private final CategoryService categoryService;

    @GetMapping("/institutions/{countryCode}")
    public ResponseEntity<List<InstitutionDto>> getCountryInstitutions(@PathVariable String countryCode) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(accountDataService.getCountryInstitutions(countryCode));
    }

    @PostMapping("/requisitions")
    public ResponseEntity<RequisitionDto> createRequisition(@RequestBody CreateRequisitionRequest createRequisitionRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountDataService.createRequisition(createRequisitionRequest.getInstitutionId(),
                        createRequisitionRequest.getRedirectUrl()));
    }

    @PostMapping("/customer/create")
    public void createCustomer(@RequestBody CreateCustomerRequest request, @RequestHeader("Authorization") String token) {
        accountDataService.createCustomer(token.substring(BEARER_PREFIX_LENGTH), request.getRequisitionId(),
                request.getInstitutionName(), request.getInstitutionLogo());
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountDto>> getCustomerAccounts(@RequestHeader("Authorization") String token) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(accountDataService.getCustomerAccounts(token.substring(BEARER_PREFIX_LENGTH)));
    }

    @GetMapping("/account/{accountExternalId}")
    public ResponseEntity<AccountFullInfoDto> accountFullInfo(@RequestHeader("Authorization") String token,
                                                              @PathVariable String accountExternalId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(accountDataService.getAccountFullInfo(token.substring(BEARER_PREFIX_LENGTH), accountExternalId));
    }

    @PostMapping("/account/{accountExternalId}/refresh")
    public void refreshAccountInfo(@RequestHeader("Authorization") String token,
                                   @PathVariable String accountExternalId) {
        accountDataService.refreshAccountInfo(token.substring(BEARER_PREFIX_LENGTH), accountExternalId);
    }

    @PostMapping("/category/create")
    public void createCategory(@RequestHeader("Authorization") String token,
                               @RequestBody CategoryDto category) {
        categoryService.createCategory(token.substring(BEARER_PREFIX_LENGTH), category);
    }

    @DeleteMapping("/category/delete/{id}")
    public void deleteCategory(@RequestHeader("Authorization") String token,
                               @PathVariable Long id) {
        categoryService.deleteCategory(token.substring(BEARER_PREFIX_LENGTH), id);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getCustomerCategories(@RequestHeader("Authorization") String token) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(categoryService.getCustomerCategories(token.substring(BEARER_PREFIX_LENGTH)));
    }

    @PostMapping("/categorize/transaction")
    public void editTransactionCategories(@RequestHeader("Authorization") String token,
                                           @RequestBody EditTransactionCategoriesRequest request) {
        categoryService.editTransactionCategories(token.substring(BEARER_PREFIX_LENGTH),
                request.getTransactionExternalId(), request.getCategoryIds());
    }

    @PostMapping("/categorize/account")
    public void categorizeAccountTransactions(@RequestHeader("Authorization") String token,
                                              @RequestBody CategorizeAccountTransactionsRequest request) {
        categoryService.categorizeAccountTransactions(token.substring(BEARER_PREFIX_LENGTH),
                request.getAccountExternalId(), request.getCategoryId());
    }
}
