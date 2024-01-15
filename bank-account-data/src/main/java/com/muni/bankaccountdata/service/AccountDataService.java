package com.muni.bankaccountdata.service;

import com.muni.bankaccountdata.api.gocardless.GoCardlessApi;
import com.muni.bankaccountdata.db.entity.Account;
import com.muni.bankaccountdata.db.entity.Customer;
import com.muni.bankaccountdata.db.repository.AccountRepository;
import com.muni.bankaccountdata.db.repository.CustomerRepository;
import com.muni.bankaccountdata.dto.*;
import com.muni.bankaccountdata.exception.ApiException;
import com.muni.bankaccountdata.model.AccessToken;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
    private AccessToken accessToken;

    public AccountDataService(GoCardlessApi goCardlessApi, CustomerRepository customerRepository,
                              AccountRepository accountRepository) {
        this.goCardlessApi = goCardlessApi;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
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

        ResponseEntity<RequisitionDto> response = goCardlessApi.createRequisition(accessToken.getAccessToken(), institutionId, redirectUrl);
        String exceptionMessage = REQUISITION_EXCEPTION_MSG.formatted(institutionId);
        validateResponse(response, exceptionMessage);

        return response.getBody();
    }

    public void createCustomer(String email, String requisitionId) {
        Customer newCustomer = customerRepository.save(Customer.builder()
                .email(email)
                .build());

        List<String> accountIds = getAccountIds(requisitionId);
        for (String accountId : accountIds) {
            AccountDetailsDto accountDetails = getAccountDetails(accountId);
            Account newAccount = Account.builder()
                    .externalId(accountId)
                    .name(accountDetails.getName())
                    .iban(accountDetails.getIban())
                    .currency(accountDetails.getCurrency())
                    .requisitionId(requisitionId)
                    .expirationDate(LocalDate.now().plusDays(DEFAULT_REQUISITION_VALIDITY_DAYS))
                    .customer(newCustomer)
                    .build();
            accountRepository.save(newAccount);
        }
    }

    public void updateRequisition(String email, String requisitionId) {
        Customer customer = customerRepository.findCustomerByEmail(email)
                .orElseThrow(() -> new ApiException("No customer found with email " + email));

        List<String> accountIds = getAccountIds(requisitionId);
        for (String accountId : accountIds) {
            Optional<Account> optionalAccount = accountRepository.findAccountByExternalId(accountId);
            if (optionalAccount.isEmpty()) {
                AccountDetailsDto accountDetails = getAccountDetails(accountId);
                Account newAccount = Account.builder()
                        .externalId(accountId)
                        .name(accountDetails.getName())
                        .iban(accountDetails.getIban())
                        .currency(accountDetails.getCurrency())
                        .requisitionId(requisitionId)
                        .expirationDate(LocalDate.now().plusDays(DEFAULT_REQUISITION_VALIDITY_DAYS))
                        .customer(customer)
                        .build();
                accountRepository.save(newAccount);
            }
            else {
                Account account = optionalAccount.get();
                account.setRequisitionId(requisitionId);
                account.setExpirationDate(LocalDate.now().plusDays(DEFAULT_REQUISITION_VALIDITY_DAYS));
                accountRepository.save(account);
            }
        }
    }

    public List<AccountDetailsDto> getCustomerAccounts(String email) {
        Customer customer = customerRepository.findCustomerByEmail(email)
                .orElseThrow(() -> new ApiException("No customer found with email " + email));

        List<Account> accounts = accountRepository.findAllByCustomer_Id(customer.getId());
        List<AccountDetailsDto> accountDetails = new LinkedList<>();
        for (Account account : accounts) {
            AccountDetailsDto accountDetailsDto = AccountDetailsDto.builder()
                    .id(account.getExternalId())
                    .iban(account.getIban())
                    .currency(account.getCurrency())
                    .name(account.getName())
                    .build();
            accountDetails.add(accountDetailsDto);
        }

        return accountDetails;
    }

    public void deleteAllRequisitions() {

    }

    public void accessToken() {
        System.out.println(accessToken.getAccessToken());
    }

    private List<String> getAccountIds(String requisitionId) {
        refreshAccessTokenIfExpired();

        ResponseEntity<AccountIdsDto> response = goCardlessApi.getRequisitionAccountIds(accessToken.getAccessToken(), requisitionId);
        String exceptionMessage = ACCOUNT_IDS_EXCEPTION_MSG.formatted(requisitionId);
        validateResponse(response, exceptionMessage);

        List<String> accountIds = response.getBody().getIds();

        return accountIds;
    }

    private AccountDetailsDto getAccountDetails(String accountId) {
        ResponseEntity<AccountDetailsDto> response = goCardlessApi.getAccountDetails(accessToken.getAccessToken(), accountId);
        String exceptionMessage = ACCOUNT_DETAILS_EXCEPTION_MSG.formatted(accountId);
        validateResponse(response, exceptionMessage);

        return response.getBody();
    }

    private BalancesDto getAccountBalances(String accountId) {
        ResponseEntity<BalancesDto> response = goCardlessApi.getAccountBalances(accessToken.getAccessToken(), accountId);
        String exceptionMessage = ACCOUNT_BALANCES_EXCEPTION_MSG.formatted(accountId);
        validateResponse(response, exceptionMessage);

        return response.getBody();
    }

    private TransactionsDto getAccountTransactions(String accountId) {
        ResponseEntity<AccountTransactionsDto> response = goCardlessApi.getAccountTransactions(accessToken.getAccessToken(), accountId);
        String exceptionMessage = ACCOUNT_TRANSACTIONS_EXCEPTION_MSG.formatted(accountId);
        validateResponse(response, exceptionMessage);

        return response.getBody().getTransactions();
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
