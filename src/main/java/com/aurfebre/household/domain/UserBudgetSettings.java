package com.aurfebre.household.domain;

import com.aurfebre.household.domain.enums.CycleType;
import jakarta.persistence.*;
import java.time.DayOfWeek;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CycleType cycleType;

    @Enumerated(EnumType.STRING)
    private DayOfWeek weekStartDay;

    public UserBudgetSettings() {}

    public UserBudgetSettings(Long userId, Integer monthStartDay, CycleType cycleType, DayOfWeek weekStartDay) {
        this.userId = userId;
        this.monthStartDay = monthStartDay;
        this.cycleType = cycleType;
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

    public Integer getMonthStartDay() {
        return monthStartDay;
    }

    public void setMonthStartDay(Integer monthStartDay) {
        this.monthStartDay = monthStartDay;
    }

    public CycleType getCycleType() {
        return cycleType;
    }

    public void setCycleType(CycleType cycleType) {
        this.cycleType = cycleType;
    }

    public DayOfWeek getWeekStartDay() {
        return weekStartDay;
    }

    public void setWeekStartDay(DayOfWeek weekStartDay) {
        this.weekStartDay = weekStartDay;
    }
}
