package com.aurfebre.household.dto;

import com.aurfebre.household.domain.FinancialGoal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class WeeklyBudgetResponse {

    private BigDecimal weeklyBudget;
    private BigDecimal weeklySpending;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<FinancialGoal> goals;

    public WeeklyBudgetResponse() {
    }

    public WeeklyBudgetResponse(BigDecimal weeklyBudget, BigDecimal weeklySpending, LocalDate startDate, LocalDate endDate, List<FinancialGoal> goals) {
        this.weeklyBudget = weeklyBudget;
        this.weeklySpending = weeklySpending;
        this.startDate = startDate;
        this.endDate = endDate;
        this.goals = goals;
    }

    public BigDecimal getWeeklyBudget() {
        return weeklyBudget;
    }

    public void setWeeklyBudget(BigDecimal weeklyBudget) {
        this.weeklyBudget = weeklyBudget;
    }

    public BigDecimal getWeeklySpending() {
        return weeklySpending;
    }

    public void setWeeklySpending(BigDecimal weeklySpending) {
        this.weeklySpending = weeklySpending;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<FinancialGoal> getGoals() {
        return goals;
    }

    public void setGoals(List<FinancialGoal> goals) {
        this.goals = goals;
    }
}
