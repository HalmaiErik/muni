package com.muni.bankaccountdata.api.gocardless;

import com.muni.bankaccountdata.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class GoCardlessApiImpl implements GoCardlessApi {

    @Value("${gocardless.secretid}")
    private String SECRET_ID;

    @Value("${gocardless.secretkey}")
    private String SECRET_KEY;

    private final RestTemplate restTemplate;

    public GoCardlessApiImpl() {
        restTemplate = new RestTemplate();
    }

    @Override
    @PostConstruct
    public ResponseEntity<AccessTokenCreationDto> createAccessToken() {
        HttpHeaders headers = createHttpHeaders();
        Map<String, String> body = new HashMap<>();
        body.put("secret_id", SECRET_ID);
        body.put("secret_key", SECRET_KEY);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        return restTemplate.postForEntity(CREATE_ACCESS_TOKEN_URL, entity, AccessTokenCreationDto.class);
    }

    @Override
    public ResponseEntity<AccessTokenRefreshDto> refreshAccessToken(String refreshToken) {
        HttpHeaders headers = createHttpHeaders();
        Map<String, String> body = new HashMap<>();
        body.put("refresh", refreshToken);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        return restTemplate.postForEntity(REFRESH_ACCESS_TOKEN_URL, entity, AccessTokenRefreshDto.class);
    }

    @Override
    public ResponseEntity<InstitutionDto[]> getCountryInstitutions(String accessToken, String countryCode) {
        HttpHeaders headers = createAuthenticatedHttpHeaders(accessToken);
        HttpEntity entity = new HttpEntity(headers);

        return restTemplate.exchange(COUNTRY_INSTITUTIONS_URL, HttpMethod.GET, entity, InstitutionDto[].class, countryCode);
    }

    @Override
    public ResponseEntity<RequisitionDto> createRequisition(String accessToken, String institutionId, String redirectUrl) {
        HttpHeaders headers = createAuthenticatedHttpHeaders(accessToken);
        Map<String, String> body = new HashMap<>();
        body.put("institution_id", institutionId);
        body.put("redirect", redirectUrl);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        return restTemplate.postForEntity(REQUISITIONS_URL, entity, RequisitionDto.class);
    }

    @Override
    public ResponseEntity<AccountIdsDto> getRequisitionAccountIds(String accessToken, String requisitionId) {
        HttpHeaders headers = createAuthenticatedHttpHeaders(accessToken);
        HttpEntity entity = new HttpEntity(headers);

        return restTemplate.exchange(ACCOUNT_IDS_URL, HttpMethod.GET, entity, AccountIdsDto.class, requisitionId);
    }

    @Override
    public ResponseEntity<AccountDetailsDto> getAccountDetails(String accessToken, String accountId) {
        HttpHeaders headers = createAuthenticatedHttpHeaders(accessToken);
        HttpEntity entity = new HttpEntity(headers);

        return restTemplate.exchange(ACCOUNT_DETAILS_URL, HttpMethod.GET, entity, AccountDetailsDto.class, accountId);
    }

    @Override
    public ResponseEntity<BalancesDto> getAccountBalances(String accessToken, String accountId) {
        HttpHeaders headers = createAuthenticatedHttpHeaders(accessToken);
        HttpEntity entity = new HttpEntity(headers);

        return restTemplate.exchange(ACCOUNT_BALANCES_URL, HttpMethod.GET, entity, BalancesDto.class, accountId);
    }

    @Override
    public ResponseEntity<AccountTransactionsDto> getAccountTransactions(String accessToken, String accountId) {
        HttpHeaders headers = createAuthenticatedHttpHeaders(accessToken);
        HttpEntity entity = new HttpEntity(headers);

        return restTemplate.exchange(ACCOUNT_TRANSACTIONS_URL, HttpMethod.GET, entity, AccountTransactionsDto.class, accountId);
    }

    private HttpHeaders createAuthenticatedHttpHeaders(String accessToken) {
        HttpHeaders headers = createHttpHeaders();
        headers.setBearerAuth(accessToken);
        return headers;
    }

    private HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }
}
