package com.muni.bankaccountdata.db.entity.enums;

public enum Operation {

    EQUALS("equals"),
    CONTAINS("contains");

    private String name;

    Operation(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
