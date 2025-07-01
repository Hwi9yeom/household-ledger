package com.aurfebre.household.domain.enums;

public enum Frequency {
    DAILY("매일"),
    WEEKLY("매주"),
    MONTHLY("매월"),
    YEARLY("매년");

    private final String description;

    Frequency(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}