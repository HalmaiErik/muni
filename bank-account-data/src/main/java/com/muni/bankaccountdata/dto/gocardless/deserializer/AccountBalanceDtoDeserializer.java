package com.muni.bankaccountdata.dto.gocardless.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.muni.bankaccountdata.api.frankfurter.FrankfurterApi;
import com.muni.bankaccountdata.api.frankfurter.FrankfurterApiImpl;
import com.muni.bankaccountdata.dto.frankfurter.ConversionRateToUsd;
import com.muni.bankaccountdata.dto.gocardless.AccountBalanceDto;
import com.muni.bankaccountdata.dto.util.DeserializerUtil;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public class AccountBalanceDtoDeserializer extends JsonDeserializer<AccountBalanceDto> {

    private static final String BALANCES = "balances";
    private static final String BALANCE_AMOUNT = "balanceAmount";

    private final FrankfurterApi frankfurterApi = new FrankfurterApiImpl();

    @Override
    public AccountBalanceDto deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JacksonException {
        JsonNode node = (JsonNode) jsonParser.getCodec().readTree(jsonParser).get(BALANCES).get(0).get(BALANCE_AMOUNT);

        Double amount = node.get(DeserializerUtil.AMOUNT).asDouble();
        String currency = node.get(DeserializerUtil.CURRENCY).asText();

        ResponseEntity<ConversionRateToUsd> conversion = frankfurterApi.getConversionRateToUsd(currency);
        Double currencyConversionRateToUsd = conversion.getBody().getRate();

        return AccountBalanceDto.builder()
                .amount(Double.valueOf(DeserializerUtil.DECIMAL_FORMAT.format(amount * currencyConversionRateToUsd)))
                .build();
    }
}
