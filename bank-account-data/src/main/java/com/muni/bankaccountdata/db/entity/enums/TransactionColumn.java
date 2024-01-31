package com.muni.bankaccountdata.db.entity.enums;

public enum TransactionColumn {

    AMOUNT("Amount"),
    REMITTANCE_INFO("RemittanceInfo");

    private String name;

    TransactionColumn(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
