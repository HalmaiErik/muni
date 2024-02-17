package com.muni.bankaccountdata.request;

import lombok.Data;

import java.util.List;

@Data
public class EditTransactionCategoriesRequest {

    private List<Long> categoryIds;

    private String transactionExternalId;
}
