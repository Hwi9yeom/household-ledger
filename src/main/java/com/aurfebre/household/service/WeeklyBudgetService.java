package com.aurfebre.household.service;

import com.aurfebre.household.domain.enums.EntryType;
import com.aurfebre.household.repository.LedgerEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Service
@Transactional(readOnly = true)
public class WeeklyBudgetService {

    private final LedgerEntryRepository ledgerEntryRepository;

    public WeeklyBudgetService(LedgerEntryRepository ledgerEntryRepository) {
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    public WeeklyBudgetSummary calculateWeeklyBudget(Long userId, BigDecimal annualSalary, DayOfWeek weekStartDay, LocalDate currentWeek) {
        BigDecimal weeklyBudget = annualSalary.divide(BigDecimal.valueOf(52), 2, RoundingMode.HALF_UP);
        LocalDate start = currentWeek.with(TemporalAdjusters.previousOrSame(weekStartDay));
        LocalDate end = start.plusDays(6);
        BigDecimal spent = ledgerEntryRepository.sumByUserIdAndEntryTypeAndDateBetween(userId, EntryType.EXPENSE, start, end);
        if (spent == null) {
            spent = BigDecimal.ZERO;
        }
        return new WeeklyBudgetSummary(weeklyBudget, spent, start, end);
    }

    public static class WeeklyBudgetSummary {
        private final BigDecimal weeklyBudget;
        private final BigDecimal weeklySpending;
        private final LocalDate startDate;
        private final LocalDate endDate;

        public WeeklyBudgetSummary(BigDecimal weeklyBudget, BigDecimal weeklySpending, LocalDate startDate, LocalDate endDate) {
            this.weeklyBudget = weeklyBudget;
            this.weeklySpending = weeklySpending;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public BigDecimal getWeeklyBudget() {
            return weeklyBudget;
        }

        public BigDecimal getWeeklySpending() {
            return weeklySpending;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }
    }
}
