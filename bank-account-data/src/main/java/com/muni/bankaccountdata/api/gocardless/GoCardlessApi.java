package com.muni.bankaccountdata.api.gocardless;

import com.muni.bankaccountdata.api.Api;
import com.muni.bankaccountdata.dto.gocardless.AccountBalanceDto;
import com.muni.bankaccountdata.dto.gocardless.AccountDetailsDto;
import com.muni.bankaccountdata.dto.gocardless.AccountIdListDto;
import com.muni.bankaccountdata.dto.gocardless.AccountTransactionListDto;
import com.muni.bankaccountdata.dto.shared.AccessTokenCreationDto;
import com.muni.bankaccountdata.dto.shared.AccessTokenRefreshDto;
import com.muni.bankaccountdata.dto.shared.InstitutionDto;
import com.muni.bankaccountdata.dto.shared.RequisitionDto;
import org.springframework.http.ResponseEntity;

public interface GoCardlessApi extends Api {

    String API_BASE_URL = "https://bankaccountdata.gocardless.com/api/v2";
    String CREATE_ACCESS_TOKEN_URL = API_BASE_URL + "/token/new/";
    String REFRESH_ACCESS_TOKEN_URL = API_BASE_URL + "/token/refresh/";
    String COUNTRY_INSTITUTIONS_URL = API_BASE_URL + "/institutions/?country={countryCode}";
    String REQUISITIONS_URL = API_BASE_URL + "/requisitions/";
    String ACCOUNT_IDS_URL = API_BASE_URL + "/requisitions/{requisitionId}/";
    String ACCOUNT_DETAILS_URL = API_BASE_URL + "/accounts/{accountId}/details/";
    String ACCOUNT_BALANCES_URL = API_BASE_URL + "/accounts/{accountId}/balances/";
    String ACCOUNT_TRANSACTIONS_URL = API_BASE_URL + "/accounts/{accountId}/transactions/";

    ResponseEntity<AccessTokenCreationDto> createAccessToken();
    ResponseEntity<AccessTokenRefreshDto> refreshAccessToken(String refreshToken);
    ResponseEntity<InstitutionDto[]> getCountryInstitutions(String accessToken, String countryCode);
    ResponseEntity<RequisitionDto> createRequisition(String accessToken, String userReference, String institutionId);
    ResponseEntity<AccountIdListDto> getRequisitionAccountIds(String accessToken, String requisitionId);
    ResponseEntity<AccountDetailsDto> getAccountDetails(String accessToken, String accountId);
    ResponseEntity<AccountBalanceDto> getAccountBalance(String accessToken, String accountId);
    ResponseEntity<AccountTransactionListDto> getAccountTransactions(String accessToken, String accountId);
}
