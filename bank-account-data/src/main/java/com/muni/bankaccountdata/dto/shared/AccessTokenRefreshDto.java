package com.muni.bankaccountdata.dto.shared;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccessTokenRefreshDto {

    @JsonProperty("access")
    private String access;

    @JsonProperty("access_expires")
    private int accessExpires;
}
