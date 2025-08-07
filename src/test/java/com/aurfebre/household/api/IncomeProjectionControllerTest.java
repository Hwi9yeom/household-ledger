package com.aurfebre.household.api;

import com.aurfebre.household.domain.IncomeProjection;
import com.aurfebre.household.dto.IncomeProjectionRequest;
import com.aurfebre.household.dto.IncomeProjectionResponse;
import com.aurfebre.household.service.IncomeProjectionService;
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

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IncomeProjectionController.class)
@Import(com.aurfebre.household.config.SecurityConfig.class)
class IncomeProjectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IncomeProjectionService incomeProjectionService;

    @Autowired
    private ObjectMapper objectMapper;

    private IncomeProjectionRequest validRequest;
    private IncomeProjectionResponse testResponse;

    @BeforeEach
    void setUp() {
        validRequest = IncomeProjectionRequest.builder()
                .monthlyIncome(new BigDecimal("5000"))
                .year(2025)
                .startMonth(1)
                .endMonth(12)
                .incomeType(IncomeProjection.IncomeType.SALARY)
                .description("Test")
                .build();

        testResponse = IncomeProjectionResponse.builder()
                .id(1L)
                .userId(1L)
                .monthlyIncome(new BigDecimal("5000"))
                .year(2025)
                .projectedAnnualIncome(new BigDecimal("60000"))
                .startMonth(1)
                .endMonth(12)
                .incomeType(IncomeProjection.IncomeType.SALARY)
                .description("Test")
                .isActive(true)
                .build();
    }

    @Test
    void createProjection_WhenValid_ShouldReturnCreated() throws Exception {
        when(incomeProjectionService.createProjection(any(IncomeProjectionRequest.class))).thenReturn(testResponse);

        mockMvc.perform(post("/api/income-projections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.projectedAnnualIncome", is(60000)));
    }

    @Test
    void createProjection_WhenInvalidMonthRange_ShouldReturnBadRequest() throws Exception {
        IncomeProjectionRequest invalidRequest = IncomeProjectionRequest.builder()
                .monthlyIncome(new BigDecimal("5000"))
                .year(2025)
                .startMonth(5)
                .endMonth(3)
                .incomeType(IncomeProjection.IncomeType.SALARY)
                .build();

        mockMvc.perform(post("/api/income-projections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProjectionsByYear_WhenInvalidYear_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/income-projections/year/2010"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProjectionsByYearRange_WhenStartGreaterThanEnd_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/income-projections/range")
                .param("startYear", "2025")
                .param("endYear", "2024"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void calculateAnnualIncome_WhenStartMonthAfterEndMonth_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/income-projections/calculate")
                .param("monthlyIncome", "5000")
                .param("startMonth", "6")
                .param("endMonth", "3"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void calculateAnnualIncome_WithValidInput_ShouldReturnProjectedIncome() throws Exception {
        mockMvc.perform(post("/api/income-projections/calculate")
                .param("monthlyIncome", "5000")
                .param("startMonth", "1")
                .param("endMonth", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectedAnnualIncome", is(15000)))
                .andExpect(jsonPath("$.monthCount", is(3)));
    }
}

