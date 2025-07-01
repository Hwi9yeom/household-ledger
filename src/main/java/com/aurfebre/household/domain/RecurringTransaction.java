package com.aurfebre.household.domain;

import com.aurfebre.household.domain.enums.Frequency;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "recurring_transactions")
public class RecurringTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long categoryId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Frequency frequency;

    @Column(nullable = false)
    private Integer dayOfPeriod; // 1-31 for monthly, 1-7 for weekly

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate; // nullable

    @Column(nullable = false)
    private Boolean isActive;

    private LocalDate lastProcessedDate;

    @Column(nullable = false)
    private LocalDate nextScheduledDate;

    public RecurringTransaction() {
        this.isActive = true;
    }

    public RecurringTransaction(Long userId, Long categoryId, String name, BigDecimal amount,
                               Frequency frequency, Integer dayOfPeriod, LocalDate startDate,
                               LocalDate nextScheduledDate) {
        this();
        this.userId = userId;
        this.categoryId = categoryId;
        this.name = name;
        this.amount = amount;
        this.frequency = frequency;
        this.dayOfPeriod = dayOfPeriod;
        this.startDate = startDate;
        this.nextScheduledDate = nextScheduledDate;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public Integer getDayOfPeriod() {
        return dayOfPeriod;
    }

    public void setDayOfPeriod(Integer dayOfPeriod) {
        this.dayOfPeriod = dayOfPeriod;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDate getLastProcessedDate() {
        return lastProcessedDate;
    }

    public void setLastProcessedDate(LocalDate lastProcessedDate) {
        this.lastProcessedDate = lastProcessedDate;
    }

    public LocalDate getNextScheduledDate() {
        return nextScheduledDate;
    }

    public void setNextScheduledDate(LocalDate nextScheduledDate) {
        this.nextScheduledDate = nextScheduledDate;
    }
}