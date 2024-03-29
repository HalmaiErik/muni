package com.muni.bankaccountdata.dto.gocardless.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.muni.bankaccountdata.dto.gocardless.AccountTransactionDto;
import com.muni.bankaccountdata.dto.gocardless.AccountTransactionListDto;
import com.muni.bankaccountdata.dto.util.DeserializerUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Component
public class AccountTransactionListDtoDeserializer extends JsonDeserializer<AccountTransactionListDto> {

    private static final String TRANSACTIONS = "transactions";
    private static final String BOOKED = "booked";
    private static final String INTERNAL_TRANSACTION_ID = "internalTransactionId";
    private static final String TRANSACTION_ID = "transactionId";
    private static final String TRANSACTION_AMOUNT = "transactionAmount";
    private static final String BOOKING_DATE = "bookingDate";
    private static final String REMITTANCE_INFORMATION_UNSTRUCTURED = "remittanceInformationUnstructured";

    @Override
    public AccountTransactionListDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode parent = (JsonNode) jsonParser.getCodec().readTree(jsonParser).get(TRANSACTIONS).get(BOOKED);

        List<AccountTransactionDto> accountTransactionDtos = new LinkedList<>();
        for (int i = 0; i < parent.size(); i++) {
            JsonNode child = parent.get(i);

            String transactionId = child.get(INTERNAL_TRANSACTION_ID).asText();
            String refFromInstitution = child.get(TRANSACTION_ID).asText();
            double amount = child.get(TRANSACTION_AMOUNT).get(DeserializerUtil.AMOUNT).asDouble();
            String currency = child.get(TRANSACTION_AMOUNT).get(DeserializerUtil.CURRENCY).asText();
            String bookingDate = child.get(BOOKING_DATE).asText();
            String description = child.has(REMITTANCE_INFORMATION_UNSTRUCTURED) ?
                    child.get(REMITTANCE_INFORMATION_UNSTRUCTURED).asText() : "";

            accountTransactionDtos.add(AccountTransactionDto.builder()
                    .transactionId(transactionId)
                    .refFromInstitution(refFromInstitution)
                    .amount(Double.valueOf(DeserializerUtil.DECIMAL_FORMAT.format(amount)))
                    .currency(currency)
                    .bookingDate(LocalDate.parse(bookingDate))
                    .remittanceInfo(description)
                    .build());
        }

        return AccountTransactionListDto.builder()
                .transactions(accountTransactionDtos)
                .build();
    }
}
