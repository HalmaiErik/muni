package com.muni.bankaccountdata.controller;

import com.muni.bankaccountdata.dto.*;
import com.muni.bankaccountdata.exception.ApiException;
import com.muni.bankaccountdata.request.CreateRequisitionRequest;
import com.muni.bankaccountdata.request.CreateCustomerRequest;
import com.muni.bankaccountdata.service.AccountDataService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bankaccount")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AccountDataController {

    private final AccountDataService accountDataService;

    @GetMapping("/institutions/{countryCode}")
    public ResponseEntity<List<InstitutionDto>> getCountryInstitutions(@PathVariable String countryCode) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(accountDataService.getCountryInstitutions(countryCode));
    }

    @PostMapping("/requisitions")
    public ResponseEntity<RequisitionDto> createRequisition(@RequestBody CreateRequisitionRequest createRequisitionRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountDataService.createRequisition(createRequisitionRequest.getInstitutionId(), createRequisitionRequest.getRedirectUrl()));
    }

    @PostMapping("/customer/create")
    public void createCustomer(@RequestBody CreateCustomerRequest createCustomerRequest) {
        accountDataService.createCustomer(createCustomerRequest.getEmail(), createCustomerRequest.getRequisitionId());
    }

    @GetMapping("/accounts/{email}")
    public ResponseEntity<List<AccountDetailsDto>> getCustomerAccounts(@PathVariable String email) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(accountDataService.getCustomerAccounts(email));
        }
        catch (ApiException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new LinkedList<>());
        }
    }

    @GetMapping("/transactions/{accountId}")
    public ResponseEntity<List<TransactionDto>> getAccountTransactions(@PathVariable String accountId) {
        return null;
    }
}
