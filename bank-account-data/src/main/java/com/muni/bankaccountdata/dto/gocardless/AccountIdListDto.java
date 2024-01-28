package com.muni.bankaccountdata.dto.gocardless;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AccountIdListDto {

    @JsonProperty("accounts")
    private List<String> ids;
}
