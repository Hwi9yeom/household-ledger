package com.aurfebre.household.api;

import com.aurfebre.household.domain.Expense;
import com.aurfebre.household.service.ExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpenseController.class)
@Import(com.aurfebre.household.config.SecurityConfig.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExpenseService expenseService;

    @Autowired
    private ObjectMapper objectMapper;

    private Expense testExpense;

    @BeforeEach
    void setUp() {
        testExpense = new Expense("Lunch", 15000, "Food");
        testExpense.setId(1L);
        testExpense.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getAllExpenses_ShouldReturnAllExpenses() throws Exception {
        when(expenseService.getAllExpenses()).thenReturn(Collections.singletonList(testExpense));

        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Lunch")));
    }

    @Test
    void createExpense_WhenValid_ShouldReturnCreated() throws Exception {
        when(expenseService.createExpense(any(Expense.class))).thenReturn(testExpense);

        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testExpense)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Lunch")));
    }

    @Test
    void createExpense_WhenInvalidAmount_ShouldReturnBadRequest() throws Exception {
        String invalidJson = "{\"description\":\"Lunch\",\"amount\":\"abc\",\"category\":\"Food\"}";

        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}

