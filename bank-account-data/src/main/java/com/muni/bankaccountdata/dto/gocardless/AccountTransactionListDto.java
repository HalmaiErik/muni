package com.muni.bankaccountdata.dto.gocardless;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.muni.bankaccountdata.dto.gocardless.deserializer.AccountTransactionListDtoDeserializer;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonDeserialize(using = AccountTransactionListDtoDeserializer.class)
public class AccountTransactionListDto {

    private List<AccountTransactionDto> transactions;
}
