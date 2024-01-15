package com.muni.bankaccountdata.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AccessToken {
    private String accessToken;
    private LocalDateTime accessTokenExpiration;
    private String refreshToken;
    private LocalDateTime refreshTokenExpiration;
}
