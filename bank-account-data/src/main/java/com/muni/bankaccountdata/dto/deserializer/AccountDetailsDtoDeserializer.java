package com.muni.bankaccountdata.dto.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.muni.bankaccountdata.dto.AccountDetailsDto;

import java.io.IOException;

public class AccountDetailsDtoDeserializer extends JsonDeserializer<AccountDetailsDto> {
    @Override
    public AccountDetailsDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = (JsonNode) jsonParser.getCodec().readTree(jsonParser).get("account");

        String resourceId = node.get("resourceId").asText();
        String iban = node.get("iban").asText();
        String currency = node.get("currency").asText();
        String name = node.get("name").asText();

        return AccountDetailsDto.builder()
                .resourceId(resourceId)
                .iban(iban)
                .currency(currency)
                .name(name)
                .build();
    }
}
