package com.aurfebre.household.domain.enums;

public enum TransactionType {
    FIXED("고정"),
    VARIABLE("변동");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}