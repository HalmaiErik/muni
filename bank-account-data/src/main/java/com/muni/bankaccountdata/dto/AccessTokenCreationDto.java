package com.muni.bankaccountdata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccessTokenCreationDto {

    @JsonProperty("access")
    private String access;

    @JsonProperty("access_expires")
    private int accessExpires;

    @JsonProperty("refresh")
    private String refresh;

    @JsonProperty("refresh_expires")
    private int refreshExpires;
}
