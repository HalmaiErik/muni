package com.muni.bankaccountdata.dto.frankfurter.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.muni.bankaccountdata.dto.frankfurter.ConversionRateToUsd;

import java.io.IOException;

public class ConversionRateToUsdDeserializer extends JsonDeserializer<ConversionRateToUsd> {

    @Override
    public ConversionRateToUsd deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = (JsonNode) jsonParser.getCodec().readTree(jsonParser).get("rates");

        Double amount = node.get("USD").asDouble();

        return ConversionRateToUsd.builder()
                .rate(amount)
                .build();
    }
}
