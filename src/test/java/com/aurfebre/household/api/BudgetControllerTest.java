package com.aurfebre.household.api;

import com.aurfebre.household.domain.Budget;
import com.aurfebre.household.domain.enums.BudgetPeriod;
import com.aurfebre.household.service.BudgetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BudgetController.class)
class BudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BudgetService budgetService;

    @Autowired
    private ObjectMapper objectMapper;

    private Budget testBudget;
    private List<Budget> testBudgets;

    @BeforeEach
    void setUp() {
        testBudget = new Budget(
            1L,
            1L,
            BudgetPeriod.MONTHLY,
            new BigDecimal("500000"),
            LocalDate.of(2025, 7, 1),
            LocalDate.of(2025, 7, 31)
        );
        testBudget.setId(1L);
        testBudget.setCreatedAt(LocalDateTime.now());

        Budget weeklyBudget = new Budget(
            1L,
            2L,
            BudgetPeriod.WEEKLY,
            new BigDecimal("100000"),
            LocalDate.of(2025, 7, 1),
            LocalDate.of(2025, 7, 7)
        );
        weeklyBudget.setId(2L);

        testBudgets = Arrays.asList(testBudget, weeklyBudget);
    }

    @Test
    void getAllBudgets_ShouldReturnAllBudgets() throws Exception {
        when(budgetService.getAllBudgets()).thenReturn(testBudgets);

        mockMvc.perform(get("/api/budgets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].period", is("MONTHLY")))
                .andExpect(jsonPath("$[0].amount", is(500000)))
                .andExpect(jsonPath("$[1].period", is("WEEKLY")));
    }

    @Test
    void getBudgetsByUserId_ShouldReturnUserBudgets() throws Exception {
        when(budgetService.getBudgetsByUserId(1L)).thenReturn(testBudgets);

        mockMvc.perform(get("/api/budgets/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userId", is(1)))
                .andExpect(jsonPath("$[1].userId", is(1)));
    }

    @Test
    void getBudgetsByUserIdAndPeriod_ShouldReturnFilteredBudgets() throws Exception {
        List<Budget> monthlyBudgets = Arrays.asList(testBudget);
        when(budgetService.getBudgetsByUserIdAndPeriod(1L, BudgetPeriod.MONTHLY)).thenReturn(monthlyBudgets);

        mockMvc.perform(get("/api/budgets/user/1/period/MONTHLY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].period", is("MONTHLY")));
    }

    @Test
    void getBudgetByUserIdAndCategory_ShouldReturnCategoryBudget() throws Exception {
        when(budgetService.getBudgetByUserIdAndCategory(1L, 1L)).thenReturn(Optional.of(testBudget));

        mockMvc.perform(get("/api/budgets/user/1/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.categoryId", is(1)));
    }

    @Test
    void getBudgetByUserIdAndCategory_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(budgetService.getBudgetByUserIdAndCategory(1L, 999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/budgets/user/1/category/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getActiveBudgetsForDate_WithDateParam_ShouldReturnActiveBudgets() throws Exception {
        LocalDate testDate = LocalDate.of(2025, 7, 15);
        when(budgetService.getActiveBudgetsForDate(1L, testDate)).thenReturn(testBudgets);

        mockMvc.perform(get("/api/budgets/user/1/active")
                .param("date", "2025-07-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getActiveBudgetsForDate_WithoutDateParam_ShouldUseToday() throws Exception {
        LocalDate today = LocalDate.now();
        when(budgetService.getActiveBudgetsForDate(1L, today)).thenReturn(testBudgets);

        mockMvc.perform(get("/api/budgets/user/1/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getActiveBudgetForCategoryAndDate_ShouldReturnBudget() throws Exception {
        LocalDate testDate = LocalDate.of(2025, 7, 15);
        when(budgetService.getActiveBudgetForCategoryAndDate(1L, 1L, testDate))
                .thenReturn(Optional.of(testBudget));

        mockMvc.perform(get("/api/budgets/user/1/category/1/active")
                .param("date", "2025-07-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void getBudgetById_WhenBudgetExists_ShouldReturnBudget() throws Exception {
        when(budgetService.getBudgetById(1L)).thenReturn(Optional.of(testBudget));

        mockMvc.perform(get("/api/budgets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.period", is("MONTHLY")))
                .andExpect(jsonPath("$.amount", is(500000)));
    }

    @Test
    void getBudgetById_WhenBudgetNotExists_ShouldReturnNotFound() throws Exception {
        when(budgetService.getBudgetById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/budgets/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBudget_WhenValidBudget_ShouldReturnCreated() throws Exception {
        Budget newBudget = new Budget(
            1L, 2L, BudgetPeriod.WEEKLY, new BigDecimal("100000"),
            LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 7)
        );
        newBudget.setId(3L);

        when(budgetService.createBudget(any(Budget.class))).thenReturn(newBudget);

        mockMvc.perform(post("/api/budgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBudget)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.period", is("WEEKLY")))
                .andExpect(jsonPath("$.amount", is(100000)));
    }


    @Test
    void updateBudget_WhenValidUpdate_ShouldReturnUpdatedBudget() throws Exception {
        Budget updatedBudget = new Budget(
            1L, 2L, BudgetPeriod.WEEKLY, new BigDecimal("300000"),
            LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 7)
        );
        updatedBudget.setId(1L);

        when(budgetService.updateBudget(eq(1L), any(Budget.class))).thenReturn(updatedBudget);

        mockMvc.perform(put("/api/budgets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBudget)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.period", is("WEEKLY")))
                .andExpect(jsonPath("$.amount", is(300000)));
    }

    @Test
    void updateBudget_WhenBudgetNotExists_ShouldReturnNotFound() throws Exception {
        when(budgetService.updateBudget(eq(999L), any(Budget.class)))
                .thenThrow(new IllegalArgumentException("Budget not found with id: 999"));

        mockMvc.perform(put("/api/budgets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBudget)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBudget_WhenBudgetExists_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/budgets/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBudget_WhenBudgetNotExists_ShouldReturnNotFound() throws Exception {
        doThrow(new IllegalArgumentException("Budget not found with id: 999"))
                .when(budgetService).deleteBudget(999L);

        mockMvc.perform(delete("/api/budgets/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void hardDeleteBudget_WhenBudgetExists_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/budgets/1/hard"))
                .andExpect(status().isNoContent());
    }

    @Test
    void hardDeleteBudget_WhenBudgetNotExists_ShouldReturnNotFound() throws Exception {
        doThrow(new IllegalArgumentException("Budget not found with id: 999"))
                .when(budgetService).hardDeleteBudget(999L);

        mockMvc.perform(delete("/api/budgets/999/hard"))
                .andExpect(status().isNotFound());
    }

    @Test
    void activateBudget_WhenBudgetExists_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(put("/api/budgets/1/activate"))
                .andExpect(status().isNoContent());

        verify(budgetService).activateBudget(1L);
    }

    @Test
    void activateBudget_WhenBudgetNotExists_ShouldReturnNotFound() throws Exception {
        doThrow(new IllegalArgumentException("Budget not found with id: 999"))
                .when(budgetService).activateBudget(999L);

        mockMvc.perform(put("/api/budgets/999/activate"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBudgetsByUserIdAndPeriod_WhenInvalidPeriod_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/budgets/user/1/period/INVALID_PERIOD"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getActiveBudgetsForDate_WhenInvalidDateFormat_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/budgets/user/1/active")
                .param("date", "invalid-date"))
                .andExpect(status().isBadRequest());
    }
}