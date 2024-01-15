package com.muni.bankaccountdata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.muni.bankaccountdata.dto.deserializer.AccountDetailsDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
@JsonDeserialize(using = AccountDetailsDeserializer.class)
public class AccountDetailsDto {

    @JsonProperty("resourceId")
    private String id;

    @JsonProperty("iban")
    private String iban;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("name")
    private String name;
}
