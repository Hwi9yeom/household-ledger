package com.aurfebre.household.service;

import com.aurfebre.household.domain.LedgerEntry;
import com.aurfebre.household.domain.enums.EntryType;
import com.aurfebre.household.domain.enums.TransactionType;
import com.aurfebre.household.dto.MonthlyExpenseComparison;
import com.aurfebre.household.dto.MonthlyExpenseSummary;
import com.aurfebre.household.repository.LedgerEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MonthlyExpenseServiceTest {

    @Mock
    private LedgerEntryRepository ledgerEntryRepository;

    @InjectMocks
    private MonthlyExpenseService monthlyExpenseService;

    private List<LedgerEntry> testExpenses;
    private YearMonth testYearMonth;

    @BeforeEach
    void setUp() {
        testYearMonth = YearMonth.of(2025, 7);
        
        LedgerEntry expense1 = new LedgerEntry(
            1L, EntryType.EXPENSE, TransactionType.VARIABLE, 1L,
            new BigDecimal("50000"), LocalDate.of(2025, 7, 5), "식비"
        );
        expense1.setId(1L);

        LedgerEntry expense2 = new LedgerEntry(
            1L, EntryType.EXPENSE, TransactionType.FIXED, 2L,
            new BigDecimal("800000"), LocalDate.of(2025, 7, 1), "월세"
        );
        expense2.setId(2L);

        testExpenses = Arrays.asList(expense1, expense2);
    }

    @Test
    void getMonthlyExpenses_ShouldReturnExpensesForSpecificMonth() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 7, 1);
        LocalDate endDate = LocalDate.of(2025, 7, 31);
        when(ledgerEntryRepository.findByUserIdAndEntryTypeAndDateBetweenOrderByDateDesc(
            1L, EntryType.EXPENSE, startDate, endDate)).thenReturn(testExpenses);

        // When
        List<LedgerEntry> result = monthlyExpenseService.getMonthlyExpenses(1L, testYearMonth);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(testExpenses);
        verify(ledgerEntryRepository).findByUserIdAndEntryTypeAndDateBetweenOrderByDateDesc(
            1L, EntryType.EXPENSE, startDate, endDate);
    }

    @Test
    void getMonthlyExpensesSummary_ShouldReturnCategorizedSummary() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 7, 1);
        LocalDate endDate = LocalDate.of(2025, 7, 31);
        when(ledgerEntryRepository.findByUserIdAndEntryTypeAndDateBetweenOrderByDateDesc(
            1L, EntryType.EXPENSE, startDate, endDate)).thenReturn(testExpenses);

        // When
        MonthlyExpenseSummary result = monthlyExpenseService.getMonthlyExpensesSummary(1L, testYearMonth);

        // Then
        assertThat(result.categoryExpenses()).containsEntry("식비", new BigDecimal("50000"));
        assertThat(result.categoryExpenses()).containsEntry("월세", new BigDecimal("800000"));
        assertThat(result.total()).isEqualTo(new BigDecimal("850000"));
        assertThat(result.categoryExpenses()).hasSize(2);
    }

    @Test
    void getMonthlyComparison_ShouldReturnComparisonWithPreviousMonth() {
        // Given
        LocalDate currentStart = LocalDate.of(2025, 7, 1);
        LocalDate currentEnd = LocalDate.of(2025, 7, 31);
        LocalDate previousStart = LocalDate.of(2025, 6, 1);
        LocalDate previousEnd = LocalDate.of(2025, 6, 30);
        
        when(ledgerEntryRepository.sumByUserIdAndEntryTypeAndDateBetween(
            1L, EntryType.EXPENSE, currentStart, currentEnd))
            .thenReturn(new BigDecimal("850000"));
        when(ledgerEntryRepository.sumByUserIdAndEntryTypeAndDateBetween(
            1L, EntryType.EXPENSE, previousStart, previousEnd))
            .thenReturn(new BigDecimal("750000"));

        // When
        MonthlyExpenseComparison result = monthlyExpenseService.getMonthlyComparison(1L, testYearMonth);

        // Then
        assertThat(result.currentMonth()).isEqualTo(new BigDecimal("850000"));
        assertThat(result.previousMonth()).isEqualTo(new BigDecimal("750000"));
        assertThat(result.difference()).isEqualTo(new BigDecimal("100000"));
        assertThat(result.percentageChange()).isCloseTo(13.33, within(0.01));
    }

    @Test
    void getMonthlyComparison_WhenPreviousMonthIsZero_ShouldHandleZeroDivision() {
        // Given
        LocalDate currentStart = LocalDate.of(2025, 7, 1);
        LocalDate currentEnd = LocalDate.of(2025, 7, 31);
        LocalDate previousStart = LocalDate.of(2025, 6, 1);
        LocalDate previousEnd = LocalDate.of(2025, 6, 30);
        
        when(ledgerEntryRepository.sumByUserIdAndEntryTypeAndDateBetween(
            1L, EntryType.EXPENSE, currentStart, currentEnd))
            .thenReturn(new BigDecimal("850000"));
        when(ledgerEntryRepository.sumByUserIdAndEntryTypeAndDateBetween(
            1L, EntryType.EXPENSE, previousStart, previousEnd))
            .thenReturn(BigDecimal.ZERO);

        // When
        MonthlyExpenseComparison result = monthlyExpenseService.getMonthlyComparison(1L, testYearMonth);

        // Then
        assertThat(result.currentMonth()).isEqualTo(new BigDecimal("850000"));
        assertThat(result.previousMonth()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.difference()).isEqualTo(new BigDecimal("850000"));
        assertThat(result.percentageChange()).isEqualTo(0.0);
    }

    @Test
    void getMonthlyComparison_WhenNoDataExists_ShouldReturnZeroValues() {
        // Given
        when(ledgerEntryRepository.sumByUserIdAndEntryTypeAndDateBetween(
            eq(1L), eq(EntryType.EXPENSE), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(null);

        // When
        MonthlyExpenseComparison result = monthlyExpenseService.getMonthlyComparison(1L, testYearMonth);

        // Then
        assertThat(result.currentMonth()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.previousMonth()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.difference()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.percentageChange()).isEqualTo(0.0);
    }

    @Test
    void getMonthlyExpensesSummary_WhenNoExpenses_ShouldReturnEmptyWithZeroTotal() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 7, 1);
        LocalDate endDate = LocalDate.of(2025, 7, 31);
        when(ledgerEntryRepository.findByUserIdAndEntryTypeAndDateBetweenOrderByDateDesc(
            1L, EntryType.EXPENSE, startDate, endDate)).thenReturn(List.of());

        // When
        MonthlyExpenseSummary result = monthlyExpenseService.getMonthlyExpensesSummary(1L, testYearMonth);

        // Then
        assertThat(result.total()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.categoryExpenses()).isEmpty();
    }
}