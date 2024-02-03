package com.muni.bankaccountdata.mapper;

import com.muni.bankaccountdata.db.entity.Account;
import com.muni.bankaccountdata.db.entity.Customer;
import com.muni.bankaccountdata.dto.gocardless.AccountDetailsDto;
import com.muni.bankaccountdata.dto.internal.AccountDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AccountMapper {

    private static final int DEFAULT_REQUISITION_VALIDITY_DAYS = 90;

    public static Account apiDtoToEntity(AccountDetailsDto accountDetailsDto, String accountExternalId, String requisitionId,
                                         String institutionLogo, String institutionName, Customer customer) {
        return Account.builder()
                .externalId(accountExternalId)
                .name(accountDetailsDto.getName())
                .iban(accountDetailsDto.getIban())
                .currency(accountDetailsDto.getCurrency())
                .requisitionId(requisitionId)
                .expirationDate(LocalDate.now().plusDays(DEFAULT_REQUISITION_VALIDITY_DAYS))
                .institutionName(institutionName)
                .institutionLogo(institutionLogo)
                .customer(customer)
                .build();
    }

    public static AccountDto entityToInternalDto(Account account) {
        return AccountDto.builder()
                .externalId(account.getExternalId())
                .name(account.getName())
                .iban(account.getIban())
                .currency(account.getCurrency())
                .status(account.getExpirationDate().isAfter(LocalDate.now()) ? AccountDto.Status.ACTIVE : AccountDto.Status.EXPIRED)
                .institutionName(account.getInstitutionName())
                .institutionLogo(account.getInstitutionLogo())
                .balance(account.getBalance())
                .build();
    }
}
