package com.muni.bankaccountdata.controller;

import com.muni.bankaccountdata.dto.internal.AccountDto;
import com.muni.bankaccountdata.dto.internal.TransactionDto;
import com.muni.bankaccountdata.dto.shared.InstitutionDto;
import com.muni.bankaccountdata.dto.shared.RequisitionDto;
import com.muni.bankaccountdata.exception.ApiException;
import com.muni.bankaccountdata.request.CreateCustomerRequest;
import com.muni.bankaccountdata.request.CreateRequisitionRequest;
import com.muni.bankaccountdata.request.GetAccountTransactionsRequest;
import com.muni.bankaccountdata.service.AccountDataService;
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

    private final AccountDataService accountDataService;

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
    public void createCustomer(@RequestBody CreateCustomerRequest request) {
        accountDataService.createCustomer(request.getEmail(), request.getRequisitionId(),
                request.getInstitutionName(), request.getInstitutionLogo());
    }

    @GetMapping("/accounts/{email}")
    public ResponseEntity<List<AccountDto>> getCustomerAccounts(@PathVariable String email) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(accountDataService.getCustomerAccounts(email));
    }

    @PostMapping("/account/transactions")
    public ResponseEntity<List<TransactionDto>> accountTransactions(@RequestBody GetAccountTransactionsRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(accountDataService.getAccountTransactions(request.getEmail(), request.getAccountExternalId(), request.isRefresh()));
    }
}
