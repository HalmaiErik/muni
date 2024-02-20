package com.muni.bankaccountdata.dto.gocardless.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.muni.bankaccountdata.dto.gocardless.AccountDetailsDto;

import java.io.IOException;

public class AccountDetailsDtoDeserializer extends JsonDeserializer<AccountDetailsDto> {

    private static final String ACCOUNT = "account";
    private static final String IBAN = "iban";
    private static final String CURRENCY = "currency";
    private static final String NAME = "name";

    @Override
    public AccountDetailsDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = (JsonNode) jsonParser.getCodec().readTree(jsonParser).get(ACCOUNT);

        String iban = node.get(IBAN).asText();
        String currency = node.get(CURRENCY).asText();

        JsonNode nameNode = node.get(NAME);
        String name = null;
        if (nameNode != null) {
            name = nameNode.asText();
        }

        return AccountDetailsDto.builder()
                .iban(iban)
                .currency(currency)
                .name(name)
                .build();
    }
}
