package com.muni.bankaccountdata.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateRequisitionRequest {

    @JsonProperty("institutionId")
    private String institutionId;

    @JsonProperty("redirectUrl")
    private String redirectUrl;
}
