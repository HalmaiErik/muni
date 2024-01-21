package com.muni.bankaccountdata.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.muni.bankaccountdata.dto.deserializer.AccountDetailsDtoDeserializer;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(using = AccountDetailsDtoDeserializer.class)
public class AccountDetailsDto {

    private String resourceId;

    private String iban;

    private String currency;

    private String name;
}
