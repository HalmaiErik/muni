package com.muni.bankaccountdata.service;

import com.muni.bankaccountdata.api.frankfurter.FrankfurterApi;
import com.muni.bankaccountdata.api.gocardless.GoCardlessApi;
import com.muni.bankaccountdata.db.entity.Account;
import com.muni.bankaccountdata.db.entity.Customer;
import com.muni.bankaccountdata.db.entity.Transaction;
import com.muni.bankaccountdata.db.repository.AccountRepository;
import com.muni.bankaccountdata.db.repository.CustomerRepository;
import com.muni.bankaccountdata.db.repository.TransactionRepository;
import com.muni.bankaccountdata.dto.frankfurter.ConversionRateToUsd;
import com.muni.bankaccountdata.dto.gocardless.*;
import com.muni.bankaccountdata.dto.internal.*;
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
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
    private final FrankfurterApi frankfurterApi;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerValidator customerValidator;
    private final AccountValidator accountValidator;
    private AccessToken accessToken;

    public AccountDataService(GoCardlessApi goCardlessApi, FrankfurterApi frankfurterApi,
                              CustomerRepository customerRepository, AccountRepository accountRepository,
                              TransactionRepository transactionRepository, CustomerValidator customerValidator,
                              AccountValidator accountValidator) {
        this.goCardlessApi = goCardlessApi;
        this.frankfurterApi = frankfurterApi;
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
        Customer customer = customerRepository.findCustomerByEmail(email)
                .orElseGet(() -> customerRepository.save(Customer.builder()
                                    .email(email)
                                    .build()));

        List<String> accountExternalIds = getAccountIds(requisitionId);
        List<Account> accountsToSave = new LinkedList<>();
        for (String accountExternalId : accountExternalIds) {
            AccountDetailsDto accountDetailsDto = fetchAccountDetails(accountExternalId);
            AccountBalanceDto accountBalanceDto = fetchAccountBalance(accountExternalId);
            Account newAccount = AccountMapper.apiDtoToEntity(accountDetailsDto, accountBalanceDto, accountExternalId,
                    requisitionId, institutionLogo, institutionName, customer);
            accountsToSave.add(newAccount);
        }

        accountRepository.saveAll(accountsToSave);
    }

    public List<AccountDto> getCustomerAccounts(String token) {
        try {
            Customer customer = customerValidator.validateAndGetRequiredCustomer(token);

            return getCustomerAccounts(customer);
        } catch (ApiException e) {
            return new ArrayList<>();
        }
    }

    public CustomerInfoDto getCustomerInfo(String token) {
        Customer customer = customerValidator.validateAndGetRequiredCustomer(token);

        List<AccountDto> accountDtos = getCustomerAccounts(customer);

        List<Transaction> transactions = transactionRepository.findAllByCustomer_IdOrderByBookingDateDesc(customer.getId());
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(TransactionMapper::entityToInternalDto)
                .toList();

        return CustomerInfoDto.builder()
                .accounts(accountDtos)
                .transactions(transactionDtos)
                .build();
    }

    public AccountFullInfoDto getAccountFullInfo(String token, String accountExternalId) {
        Customer customer = customerValidator.validateAndGetRequiredCustomer(token);
        Account account = accountValidator.getRequiredCustomerAccount(customer, accountExternalId);

        AccountDto accountDto = AccountMapper.entityToInternalDto(account);

        List<Transaction> transactions = transactionRepository.findAllByAccount_IdOrderByBookingDateDesc(account.getId());
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(TransactionMapper::entityToInternalDto)
                .toList();

        return AccountFullInfoDto.builder()
                .account(accountDto)
                .transactions(transactionDtos)
                .build();
    }

    public void refreshAccountInfo(String token, String accountExternalId) {
        Customer customer = customerValidator.validateAndGetRequiredCustomer(token);
        Account account = accountValidator.getRequiredCustomerAccount(customer, accountExternalId);

        try {
            ExecutorService executorService = Executors.newFixedThreadPool(2);
            executorService.execute(() -> refreshAccountTransactions(customer, account));
            executorService.execute(() -> refreshAccountBalance(account));

            executorService.shutdown();
            executorService.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new ApiException(e.getMessage());
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

    private AccountDetailsDto fetchAccountDetails(String accountExternalId) {
        ResponseEntity<AccountDetailsDto> response = goCardlessApi.getAccountDetails(accessToken.getAccessToken(), accountExternalId);
        String exceptionMessage = ACCOUNT_DETAILS_EXCEPTION_MSG.formatted(accountExternalId);
        validateResponse(response, exceptionMessage);

        return response.getBody();
    }

    private List<AccountDto> getCustomerAccounts(Customer customer) {
        List<Account> accounts = accountRepository.findAllByCustomer_IdOrderByInstitutionNameAsc(customer.getId());

        return accounts.stream()
                .map(AccountMapper::entityToInternalDto)
                .collect(Collectors.toList());
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
        Map<String, Double> currencyRateCache = new HashMap<>();
        for (AccountTransactionDto accountTransaction : accountTransactions) {
            if (transactionRepository.existsTransactionByExternalId(accountTransaction.getTransactionId())) {
                continue;
            }

            String currency = accountTransaction.getCurrency();
            Double currencyConversionRateToUsd;
            if (!currencyRateCache.containsKey(currency)) {
                Double conversionRate = frankfurterApi.getConversionRateToUsd(currency).getBody().getRate();
                currencyRateCache.put(currency, conversionRate);
                currencyConversionRateToUsd = conversionRate;
            }
            else {
                currencyConversionRateToUsd = currencyRateCache.get(currency);
            }

            Transaction newTransaction = TransactionMapper.apiDtoToEntity(accountTransaction, customer, account, currencyConversionRateToUsd);
            transactionRepository.save(newTransaction);
        }
    }

    private void refreshAccountBalance(Account account) {
        AccountBalanceDto accountBalanceDto = fetchAccountBalance(account.getExternalId());
        account.setBalance(accountBalanceDto.getAmount());
        accountRepository.save(account);
    }

    private AccountBalanceDto fetchAccountBalance(String accountExternalId) {
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
