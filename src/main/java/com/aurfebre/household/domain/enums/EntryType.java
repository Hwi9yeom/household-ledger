package com.aurfebre.household.domain.enums;

public enum EntryType {
    INCOME("수입"),
    EXPENSE("지출"),
    SAVING_INVESTMENT("저축/투자");

    private final String description;

    EntryType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}