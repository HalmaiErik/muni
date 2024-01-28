package com.muni.bankaccountdata.dto.shared;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequisitionDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("link")
    private String link;
}
