package com.aurfebre.household.domain.enums;

public enum CategoryType {
    INCOME("수입"),
    EXPENSE("지출"),
    SAVING_INVESTMENT("저축/투자");

    private final String description;

    CategoryType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}