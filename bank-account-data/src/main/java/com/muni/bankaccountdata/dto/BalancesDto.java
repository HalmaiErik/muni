package com.muni.bankaccountdata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BalancesDto {

    @JsonProperty("balances")
    List<BalanceDto> balances;
}
