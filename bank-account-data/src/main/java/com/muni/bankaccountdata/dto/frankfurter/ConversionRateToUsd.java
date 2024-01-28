package com.muni.bankaccountdata.dto.frankfurter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.muni.bankaccountdata.dto.frankfurter.deserializer.ConversionRateToUsdDeserializer;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(using = ConversionRateToUsdDeserializer.class)
public class ConversionRateToUsd {

    private Double rate;
}
