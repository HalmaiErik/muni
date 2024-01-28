package com.muni.bankaccountdata.service;

import com.muni.bankaccountdata.api.gocardless.GoCardlessApi;
import com.muni.bankaccountdata.db.entity.Account;
import com.muni.bankaccountdata.db.entity.Customer;
import com.muni.bankaccountdata.db.entity.Transaction;
import com.muni.bankaccountdata.db.repository.AccountRepository;
import com.muni.bankaccountdata.db.repository.CustomerRepository;
import com.muni.bankaccountdata.db.repository.TransactionRepository;
import com.muni.bankaccountdata.dto.gocardless.AccountDetailsDto;
import com.muni.bankaccountdata.dto.gocardless.AccountIdListDto;
import com.muni.bankaccountdata.dto.gocardless.AccountTransactionDto;
import com.muni.bankaccountdata.dto.gocardless.AccountTransactionListDto;
import com.muni.bankaccountdata.dto.internal.AccountDto;
import com.muni.bankaccountdata.dto.internal.TransactionDto;
import com.muni.bankaccountdata.dto.shared.AccessTokenCreationDto;
import com.muni.bankaccountdata.dto.shared.AccessTokenRefreshDto;
import com.muni.bankaccountdata.dto.shared.InstitutionDto;
import com.muni.bankaccountdata.dto.shared.RequisitionDto;
import com.muni.bankaccountdata.exception.ApiException;
import com.muni.bankaccountdata.model.AccessToken;
import com.muni.bankaccountdata.request.CreateCustomerRequest;
import com.muni.bankaccountdata.request.CreateRequisitionRequest;
import com.muni.bankaccountdata.request.GetAccountTransactionsRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private static final String ACCOUNT_BALANCES_EXCEPTION_MSG = "Could not get balances for account id %s";
    private static final String ACCOUNT_TRANSACTIONS_EXCEPTION_MSG = "Could not get transactions for account id %s";
    private static final int ACCESS_TOKEN_EXPIRATION_THRESHOLD = 10;
    private static final int DEFAULT_REQUISITION_VALIDITY_DAYS = 90;

    private final GoCardlessApi goCardlessApi;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private AccessToken accessToken;

    public AccountDataService(GoCardlessApi goCardlessApi, CustomerRepository customerRepository,
                              AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.goCardlessApi = goCardlessApi;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
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

    public void createCustomer(String email, String requisitionId, String institutionName, String institutionLogo) {
        Customer newCustomer = customerRepository.save(Customer.builder()
                .email(email)
                .build());

        List<String> accountExternalIds = getAccountIds(requisitionId);
        List<Account> accountsToSave = new LinkedList<>();
        for (String accountExternalId : accountExternalIds) {
            AccountDetailsDto accountDetailsDto = getAccountDetails(accountExternalId);
            Account newAccount = Account.builder()
                    .externalId(accountExternalId)
                    .name(accountDetailsDto.getName())
                    .iban(accountDetailsDto.getIban())
                    .currency(accountDetailsDto.getCurrency())
                    .requisitionId(requisitionId)
                    .expirationDate(LocalDate.now().plusDays(DEFAULT_REQUISITION_VALIDITY_DAYS))
                    .institutionName(institutionName)
                    .institutionLogo(institutionLogo)
                    .customer(newCustomer)
                    .build();
            accountsToSave.add(newAccount);
        }

        accountRepository.saveAll(accountsToSave);
    }

    public List<AccountDto> getCustomerAccounts(String email) {
        try {
            Customer customer = customerRepository.findCustomerByEmail(email)
                    .orElseThrow(() -> new ApiException("No customer found with email " + email));

            List<Account> accounts = accountRepository.findAllByCustomer_Id(customer.getId());
            return accounts.stream()
                    .map(account ->
                            AccountDto.builder()
                                    .externalId(account.getExternalId())
                                    .name(account.getName())
                                    .iban(account.getIban())
                                    .currency(account.getCurrency())
                                    .expirationDate(account.getExpirationDate())
                                    .institutionName(account.getInstitutionName())
                                    .institutionLogo(account.getInstitutionLogo())
                                    .build())
                    .collect(Collectors.toList());
        }
        catch (ApiException e) {
            return new LinkedList<>();
        }
    }

    public List<TransactionDto> getAccountTransactions(String email, String accountExternalId, boolean refresh) {
        try {
            Customer customer = customerRepository.findCustomerByEmail(email)
                    .orElseThrow(() -> new ApiException(String.format("No customer found with email %s", email)));
            Account account = accountRepository.findAccountByExternalIdAndCustomer_Id(accountExternalId, customer.getId())
                    .orElseThrow(() -> new ApiException(String.format("No account found with externalId %s and customer email %s",
                            accountExternalId, email)));

            if (refresh) {
                List<AccountTransactionDto> accountTransactions = fetchAccountTransactions(accountExternalId);
                saveNewAccountTransactions(customer, account, accountTransactions);
            }

            List<Transaction> transactions = transactionRepository.findAllByAccount_IdOrderByBookingDateDesc(account.getId());

            return transactions.stream()
                    .map(transaction ->
                            TransactionDto.builder()
                                    .refFromInstitution(transaction.getRefFromInstitution())
                                    .amount(transaction.getAmount())
                                    .bookingDate(transaction.getBookingDate())
                                    .remittanceInfo(transaction.getRemittanceInfo())
                                    .build())
                    .collect(Collectors.toList());
        }
        catch (ApiException e) {
            return new LinkedList<>();
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

    private List<AccountTransactionDto> fetchAccountTransactions(String accountExternalId) {
        ResponseEntity<AccountTransactionListDto> response = goCardlessApi.getAccountTransactions(accessToken.getAccessToken(), accountExternalId);
        String exceptionMessage = ACCOUNT_TRANSACTIONS_EXCEPTION_MSG.formatted(accountExternalId);
        validateResponse(response, exceptionMessage);

        return response.getBody().getTransactions();
    }

    private void saveNewAccountTransactions(Customer customer, Account account, List<AccountTransactionDto> accountTransactions) {
        for (AccountTransactionDto accountTransaction : accountTransactions) {
            if (accountRepository.existsAccountByExternalId(accountTransaction.getTransactionId())) {
                continue;
            }

            Transaction newTransaction = Transaction.builder()
                    .externalId(accountTransaction.getTransactionId())
                    .refFromInstitution(accountTransaction.getRefFromInstitution())
                    .amount(accountTransaction.getAmount())
                    .bookingDate(accountTransaction.getBookingDate())
                    .remittanceInfo(accountTransaction.getRemittanceInfo())
                    .customer(customer)
                    .account(account)
                    .build();
            transactionRepository.save(newTransaction);
        }
    }

//    private BalancesDto getAccountBalances(String accountId) {
//        ResponseEntity<BalancesDto> response = goCardlessApi.getAccountBalances(accessToken.getAccessToken(), accountId);
//        String exceptionMessage = ACCOUNT_BALANCES_EXCEPTION_MSG.formatted(accountId);
//        validateResponse(response, exceptionMessage);
//
//        return response.getBody();
//    }

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
