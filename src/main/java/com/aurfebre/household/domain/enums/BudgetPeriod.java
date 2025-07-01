package com.aurfebre.household.domain.enums;

public enum BudgetPeriod {
    DAILY("일간", 1),
    WEEKLY("주간", 7),
    MONTHLY("월간", 30),
    YEARLY("연간", 365);

    private final String description;
    private final int days;

    BudgetPeriod(String description, int days) {
        this.description = description;
        this.days = days;
    }

    public String getDescription() {
        return description;
    }

    public int getDays() {
        return days;
    }
}