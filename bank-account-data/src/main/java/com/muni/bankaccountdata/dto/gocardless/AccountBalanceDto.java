package com.muni.bankaccountdata.dto.gocardless;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.muni.bankaccountdata.dto.gocardless.deserializer.AccountBalanceDtoDeserializer;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(using = AccountBalanceDtoDeserializer.class)
public class AccountBalanceDto {

    private Double amount;
}
