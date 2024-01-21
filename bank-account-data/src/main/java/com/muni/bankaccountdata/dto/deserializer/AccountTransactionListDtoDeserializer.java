package com.muni.bankaccountdata.dto.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.muni.bankaccountdata.dto.AccountDetailsDto;
import com.muni.bankaccountdata.dto.AccountTransactionDto;
import com.muni.bankaccountdata.dto.AccountTransactionListDto;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class AccountTransactionListDtoDeserializer extends JsonDeserializer<AccountTransactionListDto> {

    @Override
    public AccountTransactionListDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode parent = (JsonNode) jsonParser.getCodec().readTree(jsonParser).get("transactions").get("booked");

        List<AccountTransactionDto> accountTransactionDtos = new LinkedList<>();
        for (int i = 0; i < parent.size(); i++) {
            JsonNode child = parent.get(i);

            String transactionId = child.get("transactionId").asText();
            String debtorName = child.get("debtorName").asText();
            String debtorAccount = child.get("debtorAccount").get("iban").asText();
            String creditorName = child.get("creditorName").asText();
            String creditorAccount = child.get("creditorAccount").get("iban").asText();
            Double amount = child.get("transactionAmount").get("amount").asDouble();
            String currency = child.get("transactionAmount").get("currency").asText();
            String bookingDate = child.get("bookingDate").asText();
            String description = child.get("remittanceInformationUnstructured").asText();

            CurrencyConversion conversion = MonetaryConversions.getConversion("USD");
            MonetaryAmount monetaryAmount = Monetary.getDefaultAmountFactory()
                    .setCurrency(currency).setNumber(amount).create();
            MonetaryAmount convertedToUsd = monetaryAmount.with(conversion);

            accountTransactionDtos.add(AccountTransactionDto.builder()
                    .transactionId(transactionId)
                    .debtorName(debtorName)
                    .debtorAccount(debtorAccount)
                    .creditorName(creditorName)
                    .creditorAccount(creditorAccount)
                    .amount(convertedToUsd.getNumber().doubleValueExact())
                    .bookingDate(LocalDate.parse(bookingDate))
                    .remittanceInfo(description)
                    .build());
        }

        return AccountTransactionListDto.builder()
                .transactions(accountTransactionDtos)
                .build();
    }
}
