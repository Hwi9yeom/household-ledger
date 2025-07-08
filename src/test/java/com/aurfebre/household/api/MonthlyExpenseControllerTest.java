package com.aurfebre.household.api;

import
        com.aurfebre.household.domain.LedgerEntry;
import com.aurfebre.household.domain.enums.EntryType;
import com.aurfebre.household.domain.enums.TransactionType;
import com.aurfebre.household.dto.MonthlyExpenseComparison;
import com.aurfebre.household.dto.MonthlyExpenseSummary;
import com.aurfebre.household.service.MonthlyExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MonthlyExpenseController.class)
@Import(com.aurfebre.household.config.SecurityConfig.class)
class MonthlyExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MonthlyExpenseService monthlyExpenseService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<LedgerEntry> testExpenses;

    @BeforeEach
    void setUp() {
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
    void getMonthlyExpenses_ShouldReturnExpensesForSpecificMonth() throws Exception {
        // Given
        YearMonth yearMonth = YearMonth.of(2025, 7);
        when(monthlyExpenseService.getMonthlyExpenses(1L, yearMonth)).thenReturn(testExpenses);

        // When & Then
        mockMvc.perform(get("/api/monthly-expenses/user/1")
                .param("yearMonth", "2025-07"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].entryType", is("EXPENSE")))
                .andExpect(jsonPath("$[0].amount", is(50000)))
                .andExpect(jsonPath("$[1].amount", is(800000)));
    }

    @Test
    void getMonthlyExpensesSummary_ShouldReturnCategorizedSummary() throws Exception {
        // Given
        YearMonth yearMonth = YearMonth.of(2025, 7);
        Map<String, BigDecimal> categoryExpenses = new HashMap<>();
        categoryExpenses.put("식비", new BigDecimal("50000"));
        categoryExpenses.put("주거비", new BigDecimal("800000"));
        MonthlyExpenseSummary summary = new MonthlyExpenseSummary(categoryExpenses, new BigDecimal("850000"));
        when(monthlyExpenseService.getMonthlyExpensesSummary(1L, yearMonth)).thenReturn(summary);

        // When & Then
        mockMvc.perform(get("/api/monthly-expenses/user/1/summary")
                .param("yearMonth", "2025-07"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.categoryExpenses.식비", is(50000)))
                .andExpect(jsonPath("$.categoryExpenses.주거비", is(800000)))
                .andExpect(jsonPath("$.total", is(850000)));
    }

    @Test
    void getMonthlyExpensesComparison_ShouldReturnComparisonWithPreviousMonth() throws Exception {
        // Given
        YearMonth currentMonth = YearMonth.of(2025, 7);
        MonthlyExpenseComparison comparison = new MonthlyExpenseComparison(
            new BigDecimal("850000"),
            new BigDecimal("750000"),
            new BigDecimal("100000"),
            13.33
        );
        when(monthlyExpenseService.getMonthlyComparison(1L, currentMonth)).thenReturn(comparison);

        // When & Then
        mockMvc.perform(get("/api/monthly-expenses/user/1/comparison")
                .param("yearMonth", "2025-07"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.currentMonth", is(850000)))
                .andExpect(jsonPath("$.previousMonth", is(750000)))
                .andExpect(jsonPath("$.difference", is(100000)))
                .andExpect(jsonPath("$.percentageChange", is(13.33)));
    }

    @Test
    void getMonthlyExpenses_WhenInvalidYearMonth_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/monthly-expenses/user/1")
                .param("yearMonth", "2025-13"))  // Invalid month
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMonthlyExpenses_WhenMissingParameters_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/monthly-expenses/user/1"))  // Missing yearMonth parameter
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMonthlyExpenses_WhenEmptyResult_ShouldReturnEmptyList() throws Exception {
        // Given
        YearMonth yearMonth = YearMonth.of(2025, 8);
        when(monthlyExpenseService.getMonthlyExpenses(1L, yearMonth)).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/monthly-expenses/user/1")
                .param("yearMonth", "2025-08"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getMonthlyExpenses_WhenYearBoundary_ShouldHandleDecemberToJanuary() throws Exception {
        // Given
        YearMonth yearMonth = YearMonth.of(2024, 12);
        when(monthlyExpenseService.getMonthlyExpenses(1L, yearMonth)).thenReturn(testExpenses);

        // When & Then
        mockMvc.perform(get("/api/monthly-expenses/user/1")
                .param("yearMonth", "2024-12"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getMonthlyExpenses_WhenLeapYear_ShouldHandleFebruary() throws Exception {
        // Given
        YearMonth yearMonth = YearMonth.of(2024, 2); // 2024 is a leap year
        when(monthlyExpenseService.getMonthlyExpenses(1L, yearMonth)).thenReturn(testExpenses);

        // When & Then
        mockMvc.perform(get("/api/monthly-expenses/user/1")
                .param("yearMonth", "2024-02"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getMonthlyExpenses_WhenInvalidYearMonthFormat_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/monthly-expenses/user/1")
                .param("yearMonth", "2025/07"))  // Invalid format (should be 2025-07)
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMonthlyExpenses_WhenInvalidYear_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/monthly-expenses/user/1")
                .param("yearMonth", "abc-07"))  // Invalid year
                .andExpect(status().isBadRequest());
    }
}