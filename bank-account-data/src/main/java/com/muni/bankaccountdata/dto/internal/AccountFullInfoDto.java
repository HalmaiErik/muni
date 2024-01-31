package com.muni.bankaccountdata.dto.internal;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AccountFullInfoDto {

    private AccountDto account;
    private List<TransactionDto> transactions;
}
