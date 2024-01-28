package com.muni.bankaccountdata.dto.gocardless.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.muni.bankaccountdata.api.frankfurter.FrankfurterApi;
import com.muni.bankaccountdata.api.frankfurter.FrankfurterApiImpl;
import com.muni.bankaccountdata.dto.frankfurter.ConversionRateToUsd;
import com.muni.bankaccountdata.dto.gocardless.AccountTransactionDto;
import com.muni.bankaccountdata.dto.gocardless.AccountTransactionListDto;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class AccountTransactionListDtoDeserializer extends JsonDeserializer<AccountTransactionListDto> {

    private static final String TRANSACTIONS = "transactions";
    private static final String BOOKED = "booked";
    private static final String INTERNAL_TRANSACTION_ID = "internalTransactionId";
    private static final String TRANSACTION_ID = "transactionId";
    private static final String TRANSACTION_AMOUNT = "transactionAmount";
    private static final String AMOUNT = "amount";
    private static final String CURRENCY = "currency";
    private static final String BOOKING_DATE = "bookingDate";
    private static final String REMITTANCE_INFORMATION_UNSTRUCTURED = "remittanceInformationUnstructured";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    private final FrankfurterApi frankfurterApi = new FrankfurterApiImpl();

    @Override
    public AccountTransactionListDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode parent = (JsonNode) jsonParser.getCodec().readTree(jsonParser).get(TRANSACTIONS).get(BOOKED);

        List<AccountTransactionDto> accountTransactionDtos = new LinkedList<>();
        for (int i = 0; i < parent.size(); i++) {
            JsonNode child = parent.get(i);

            String transactionId = child.get(INTERNAL_TRANSACTION_ID).asText();
            String refFromInstitution = child.get(TRANSACTION_ID).asText();
            double amount = child.get(TRANSACTION_AMOUNT).get(AMOUNT).asDouble();
            String currency = child.get(TRANSACTION_AMOUNT).get(CURRENCY).asText();
            String bookingDate = child.get(BOOKING_DATE).asText();
            String description = child.has(REMITTANCE_INFORMATION_UNSTRUCTURED) ?
                    child.get(REMITTANCE_INFORMATION_UNSTRUCTURED).asText() : "";

            ResponseEntity<ConversionRateToUsd> conversion = frankfurterApi.getConversionRateToUsd(currency);
            Double currencyConversionRateToUsd = conversion.getBody().getRate();

            accountTransactionDtos.add(AccountTransactionDto.builder()
                    .transactionId(transactionId)
                    .refFromInstitution(refFromInstitution)
                    .amount(Double.valueOf(DECIMAL_FORMAT.format(amount * currencyConversionRateToUsd)))
                    .bookingDate(LocalDate.parse(bookingDate))
                    .remittanceInfo(description)
                    .build());
        }

        return AccountTransactionListDto.builder()
                .transactions(accountTransactionDtos)
                .build();
    }
}
