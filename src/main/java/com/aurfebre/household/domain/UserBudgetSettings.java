package com.aurfebre.household.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_budget_settings")
public class UserBudgetSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private Integer monthStartDay;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal monthlySalary;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public UserBudgetSettings() {
        this.createdAt = LocalDateTime.now();
    }

    public UserBudgetSettings(Long userId, Integer monthStartDay, BigDecimal monthlySalary) {
        this();
        this.userId = userId;
        this.monthStartDay = monthStartDay;
        this.monthlySalary = monthlySalary;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
