package com.aurfebre.household.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.DayOfWeek;

@Entity
@Table(name = "user_budget_settings")
public class UserBudgetSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal annualSalary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek weekStartDay;

    public UserBudgetSettings() {
    }

    public UserBudgetSettings(Long userId, BigDecimal annualSalary, DayOfWeek weekStartDay) {
        this.userId = userId;
        this.annualSalary = annualSalary;
        this.weekStartDay = weekStartDay;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAnnualSalary() {
        return annualSalary;
    }

    public void setAnnualSalary(BigDecimal annualSalary) {
        this.annualSalary = annualSalary;
    }

    public DayOfWeek getWeekStartDay() {
        return weekStartDay;
    }

    public void setWeekStartDay(DayOfWeek weekStartDay) {
        this.weekStartDay = weekStartDay;
    }
}
