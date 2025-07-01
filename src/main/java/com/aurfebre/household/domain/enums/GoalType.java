package com.aurfebre.household.domain.enums;

public enum GoalType {
    SAVING("저축"),
    INVESTMENT("투자"),
    DEBT_PAYMENT("부채상환");

    private final String description;

    GoalType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}