package com.aurfebre.household.service;

import com.aurfebre.household.domain.enums.EntryType;
import com.aurfebre.household.repository.LedgerEntryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeeklyBudgetServiceTest {

    @Mock
    private LedgerEntryRepository ledgerEntryRepository;

    @InjectMocks
    private WeeklyBudgetService weeklyBudgetService;

    @Test
    void calculateWeeklyBudget_ShouldReturnBudgetAndSpending() {
        when(ledgerEntryRepository.sumByUserIdAndEntryTypeAndDateBetween(eq(1L), eq(EntryType.EXPENSE), any(), any()))
                .thenReturn(new BigDecimal("200"));

        var summary = weeklyBudgetService.calculateWeeklyBudget(1L, new BigDecimal("52000"),
                DayOfWeek.MONDAY, LocalDate.of(2024, 1, 3));

        assertThat(summary.getWeeklyBudget()).isEqualByComparingTo("1000.00");
        assertThat(summary.getWeeklySpending()).isEqualByComparingTo("200");
        assertThat(summary.getStartDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(summary.getEndDate()).isEqualTo(LocalDate.of(2024, 1, 7));
    }
}
