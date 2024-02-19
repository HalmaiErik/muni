package com.muni.bankaccountdata.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GetStatsRequest {

    private LocalDate from;
    private LocalDate to;
}
