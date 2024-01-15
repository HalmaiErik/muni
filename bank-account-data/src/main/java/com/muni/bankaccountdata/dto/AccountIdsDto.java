package com.muni.bankaccountdata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AccountIdsDto {

    @JsonProperty("accounts")
    private List<String> ids;
}
