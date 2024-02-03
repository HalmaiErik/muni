package com.muni.bankaccountdata.service;

import com.muni.bankaccountdata.api.gocardless.GoCardlessApi;
import com.muni.bankaccountdata.db.entity.Account;
import com.muni.bankaccountdata.db.entity.Customer;
import com.muni.bankaccountdata.db.entity.Transaction;
import com.muni.bankaccountdata.db.repository.AccountRepository;
import com.muni.bankaccountdata.db.repository.CustomerRepository;
import com.muni.bankaccountdata.db.repository.TransactionRepository;
import com.muni.bankaccountdata.dto.gocardless.*;
import com.muni.bankaccountdata.dto.internal.AccountDto;
import com.muni.bankaccountdata.dto.internal.AccountFullInfoDto;
import com.muni.bankaccountdata.dto.internal.TransactionDto;
import com.muni.bankaccountdata.dto.shared.AccessTokenCreationDto;
import com.muni.bankaccountdata.dto.shared.AccessTokenRefreshDto;
import com.muni.bankaccountdata.dto.shared.InstitutionDto;
import com.muni.bankaccountdata.dto.shared.RequisitionDto;
import com.muni.bankaccountdata.exception.ApiException;
import com.muni.bankaccountdata.mapper.AccountMapper;
import com.muni.bankaccountdata.mapper.TransactionMapper;
import com.muni.bankaccountdata.model.AccessToken;
import com.muni.bankaccountdata.validator.AccountValidator;
import com.muni.bankaccountdata.validator.CustomerValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountDataService {

    private static final String INSTITUTION_EXCEPTION_MSG = "Could not get country %s institutions";
    private static final String REQUISITION_EXCEPTION_MSG = "Could not create requisition for institution %s";
    private static final String ACCESS_TOKEN_CREATION_EXCEPTION_MSG = "Could not create new access token";
    private static final String ACCESS_TOKEN_REFRESH_EXCEPTION_MSG = "Could not refresh access token %s";
    private static final String ACCOUNT_IDS_EXCEPTION_MSG = "Could not get account ids for requisition %s";
    private static final String ACCOUNT_DETAILS_EXCEPTION_MSG = "Could not get account details for account id %s";
    private static final String ACCOUNT_BALANCE_EXCEPTION_MSG = "Could not get balance for account id %s";
    private static final String ACCOUNT_TRANSACTIONS_EXCEPTION_MSG = "Could not get transactions for account id %s";
    private static final int ACCESS_TOKEN_EXPIRATION_THRESHOLD = 10;

    private final GoCardlessApi goCardlessApi;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerValidator customerValidator;
    private final AccountValidator accountValidator;
    private AccessToken accessToken;

    public AccountDataService(GoCardlessApi goCardlessApi, CustomerRepository customerRepository,
                              AccountRepository accountRepository, TransactionRepository transactionRepository,
                              CustomerValidator customerValidator, AccountValidator accountValidator) {
        this.goCardlessApi = goCardlessApi;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.customerValidator = customerValidator;
        this.accountValidator = accountValidator;
    }

    @PostConstruct
    private void init() {
        ResponseEntity<AccessTokenCreationDto> response = goCardlessApi.createAccessToken();
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ApiException("Could not create access token");
        }
        AccessTokenCreationDto accessTokenCreationDto = response.getBody();

        buildAccessTokenModel(accessTokenCreationDto);
    }

    public List<InstitutionDto> getCountryInstitutions(String countryCode) {
        refreshAccessTokenIfExpired();

        ResponseEntity<InstitutionDto[]> response = goCardlessApi.getCountryInstitutions(accessToken.getAccessToken(), countryCode);
        String exceptionMessage = INSTITUTION_EXCEPTION_MSG.formatted(countryCode);
        validateResponse(response, exceptionMessage);

        return Arrays.asList(response.getBody());
    }

    public RequisitionDto createRequisition(String institutionId, String redirectUrl) {
        refreshAccessTokenIfExpired();

        ResponseEntity<RequisitionDto> response = goCardlessApi.createRequisition(accessToken.getAccessToken(),
                institutionId, redirectUrl);
        String exceptionMessage = REQUISITION_EXCEPTION_MSG.formatted(institutionId);
        validateResponse(response, exceptionMessage);

        return response.getBody();
    }

    public void createCustomer(String token, String requisitionId, String institutionName, String institutionLogo) {
        String email = customerValidator.getEmailFromToken(token);
        Customer newCustomer = customerRepository.save(Customer.builder()
                .email(email)
                .build());

        List<String> accountExternalIds = getAccountIds(requisitionId);
        List<Account> accountsToSave = new LinkedList<>();
        for (String accountExternalId : accountExternalIds) {
            AccountDetailsDto accountDetailsDto = getAccountDetails(accountExternalId);
            Account newAccount = AccountMapper.apiDtoToEntity(accountDetailsDto, accountExternalId, requisitionId,
                    institutionLogo, institutionName, newCustomer);
            accountsToSave.add(newAccount);
        }

        accountRepository.saveAll(accountsToSave);
    }

    public List<AccountDto> getCustomerAccounts(String token) {
        try {
            Customer customer = customerValidator.validateAndGetRequiredCustomer(token);

            List<Account> accounts = accountRepository.findAllByCustomer_Id(customer.getId());

            return accounts.stream()
                    .map(AccountMapper::entityToInternalDto)
                    .collect(Collectors.toList());
        } catch (ApiException e) {
            return new ArrayList<>();
        }
    }

    public AccountFullInfoDto getAccountFullInfo(String token, String accountExternalId, boolean refresh) {
        try {
            Customer customer = customerValidator.validateAndGetRequiredCustomer(token);
            Account account = accountValidator.getRequiredCustomerAccount(customer, accountExternalId);

            if (refresh) {
                refreshAccountTransactions(customer, account);
                refreshAccountBalance(account);
            }

            AccountDto accountDto = AccountMapper.entityToInternalDto(account);

            List<Transaction> transactions = transactionRepository.findAllByAccount_IdOrderByBookingDateDesc(account.getId());
            List<TransactionDto> transactionDtos = transactions.stream()
                    .map(TransactionMapper::entityToInternalDto)
                    .toList();

            return AccountFullInfoDto.builder()
                    .account(accountDto)
                    .transactions(transactionDtos)
                    .build();
        } catch (ApiException e) {
            return AccountFullInfoDto.builder().build();
        }
    }

    public void accessToken() {
        System.out.println(accessToken.getAccessToken());
    }

    private List<String> getAccountIds(String requisitionId) {
        refreshAccessTokenIfExpired();

        ResponseEntity<AccountIdListDto> response = goCardlessApi.getRequisitionAccountIds(accessToken.getAccessToken(), requisitionId);
        String exceptionMessage = ACCOUNT_IDS_EXCEPTION_MSG.formatted(requisitionId);
        validateResponse(response, exceptionMessage);

        List<String> accountIds = response.getBody().getIds();

        return accountIds;
    }

    private AccountDetailsDto getAccountDetails(String accountExternalId) {
        ResponseEntity<AccountDetailsDto> response = goCardlessApi.getAccountDetails(accessToken.getAccessToken(), accountExternalId);
        String exceptionMessage = ACCOUNT_DETAILS_EXCEPTION_MSG.formatted(accountExternalId);
        validateResponse(response, exceptionMessage);

        return response.getBody();
    }

    private void refreshAccountTransactions(Customer customer, Account account) {
        List<AccountTransactionDto> accountTransactions = fetchAccountTransactions(account.getExternalId());
        saveNewAccountTransactions(customer, account, accountTransactions);
    }

    private List<AccountTransactionDto> fetchAccountTransactions(String accountExternalId) {
        ResponseEntity<AccountTransactionListDto> response = goCardlessApi.getAccountTransactions(accessToken.getAccessToken(), accountExternalId);
        String exceptionMessage = ACCOUNT_TRANSACTIONS_EXCEPTION_MSG.formatted(accountExternalId);
        validateResponse(response, exceptionMessage);

        return response.getBody().getTransactions();
    }

    private void saveNewAccountTransactions(Customer customer, Account account, List<AccountTransactionDto> accountTransactions) {
        for (AccountTransactionDto accountTransaction : accountTransactions) {
            if (transactionRepository.existsTransactionByExternalId(accountTransaction.getTransactionId())) {
                continue;
            }

            Transaction newTransaction = TransactionMapper.apiDtoToEntity(accountTransaction, customer, account);
            transactionRepository.save(newTransaction);
        }
    }

    private void refreshAccountBalance(Account account) {
        AccountBalanceDto accountBalanceDto = getAccountBalance(account.getExternalId());
        account.setBalance(accountBalanceDto.getAmount());
        accountRepository.save(account);
    }

    private AccountBalanceDto getAccountBalance(String accountExternalId) {
        ResponseEntity<AccountBalanceDto> response = goCardlessApi.getAccountBalance(accessToken.getAccessToken(), accountExternalId);
        String exceptionMessage = ACCOUNT_BALANCE_EXCEPTION_MSG.formatted(accountExternalId);
        validateResponse(response, exceptionMessage);

        return response.getBody();
    }

    private void refreshAccessTokenIfExpired() {
        LocalDateTime now = LocalDateTime.now().minusMinutes(ACCESS_TOKEN_EXPIRATION_THRESHOLD);
        if (accessToken.getAccessTokenExpiration().isBefore(now)) {
            if (accessToken.getRefreshTokenExpiration().isBefore(now)) {
                ResponseEntity<AccessTokenCreationDto> response = goCardlessApi.createAccessToken();
                validateResponse(response, ACCESS_TOKEN_CREATION_EXCEPTION_MSG);
                AccessTokenCreationDto newAccessTokenCreationDto = response.getBody();
                buildAccessTokenModel(newAccessTokenCreationDto);
            }
            else {
                String refreshToken = accessToken.getRefreshToken();
                ResponseEntity<AccessTokenRefreshDto> response = goCardlessApi.refreshAccessToken(refreshToken);
                validateResponse(response, ACCESS_TOKEN_REFRESH_EXCEPTION_MSG.formatted(accessToken));
                AccessTokenRefreshDto accessTokenRefreshDto = response.getBody();
                refreshAccessTokenModel(accessTokenRefreshDto);
            }
        }
    }

    private void buildAccessTokenModel(AccessTokenCreationDto accessTokenCreationDto) {
        LocalDateTime now = LocalDateTime.now();
        accessToken = AccessToken.builder()
                .accessToken(accessTokenCreationDto.getAccess())
                .accessTokenExpiration(now.plusSeconds(accessTokenCreationDto.getAccessExpires()))
                .refreshToken(accessTokenCreationDto.getRefresh())
                .refreshTokenExpiration(now.plusSeconds(accessTokenCreationDto.getRefreshExpires()))
                .build();
    }

    private void refreshAccessTokenModel(AccessTokenRefreshDto accessTokenRefreshDto) {
        LocalDateTime now = LocalDateTime.now();
        accessToken.setAccessToken(accessTokenRefreshDto.getAccess());
        accessToken.setAccessTokenExpiration(now.plusSeconds(accessTokenRefreshDto.getAccessExpires()));
    }

    private <E> void validateResponse(ResponseEntity<E> response, String exceptionMessage) {
        if (response.getBody() == null || !response.getStatusCode().is2xxSuccessful()) {
            throw new ApiException(exceptionMessage);
        }
    }
}
