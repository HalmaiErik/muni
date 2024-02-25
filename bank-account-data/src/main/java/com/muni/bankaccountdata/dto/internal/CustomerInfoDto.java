package com.muni.bankaccountdata.dto.internal;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CustomerInfoDto {

    private List<AccountDto> accounts;

    private List<TransactionDto> transactions;
}
