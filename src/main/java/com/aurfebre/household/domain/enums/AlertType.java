package com.aurfebre.household.domain.enums;

public enum AlertType {
    WARNING("경고"),
    EXCEEDED("초과");

    private final String description;

    AlertType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}