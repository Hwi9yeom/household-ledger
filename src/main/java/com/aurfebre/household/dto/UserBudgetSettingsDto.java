package com.aurfebre.household.dto;

import java.math.BigDecimal;

public class UserBudgetSettingsDto {
    private Long userId;
    private Integer monthStartDay;
    private BigDecimal monthlySalary;

    public UserBudgetSettingsDto() {
    }

    public UserBudgetSettingsDto(Long userId, Integer monthStartDay, BigDecimal monthlySalary) {
        this.userId = userId;
        this.monthStartDay = monthStartDay;
        this.monthlySalary = monthlySalary;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getMonthStartDay() {
        return monthStartDay;
    }

    public void setMonthStartDay(Integer monthStartDay) {
        this.monthStartDay = monthStartDay;
    }

    public BigDecimal getMonthlySalary() {
        return monthlySalary;
    }

    public void setMonthlySalary(BigDecimal monthlySalary) {
        this.monthlySalary = monthlySalary;
    }
}
