package com.aurfebre.household.api;

import com.aurfebre.household.domain.UserBudgetSettings;
import com.aurfebre.household.dto.UserBudgetSettingsDto;
import com.aurfebre.household.service.UserBudgetSettingsService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserBudgetSettingsController.class)
@Import(com.aurfebre.household.config.SecurityConfig.class)
class UserBudgetSettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserBudgetSettingsService userBudgetSettingsService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserBudgetSettings settings;
    private UserBudgetSettingsDto dto;

    @BeforeEach
    void setUp() {
        settings = new UserBudgetSettings(1L, 5, new BigDecimal("1000"));
        dto = new UserBudgetSettingsDto(1L, 5, new BigDecimal("1000"));
    }

    @Test
    void getSettings_ShouldReturnSettings() throws Exception {
        when(userBudgetSettingsService.getSettingsByUserId(1L)).thenReturn(Optional.of(settings));

        mockMvc.perform(get("/api/user-settings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.monthStartDay").value(5))
                .andExpect(jsonPath("$.monthlySalary").value(1000));
    }

    @Test
    void getSettings_WhenNotFound_ShouldReturn404() throws Exception {
        when(userBudgetSettingsService.getSettingsByUserId(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/user-settings/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createSettings_ShouldReturnCreated() throws Exception {
        when(userBudgetSettingsService.createSettings(any(UserBudgetSettings.class))).thenReturn(settings);

        mockMvc.perform(post("/api/user-settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.monthStartDay").value(5));
    }

    @Test
    void createSettings_WhenInvalidMonthStartDay_ShouldReturnBadRequest() throws Exception {
        when(userBudgetSettingsService.createSettings(any(UserBudgetSettings.class)))
                .thenThrow(new IllegalArgumentException("monthStartDay must be 28 or less"));
        UserBudgetSettingsDto invalidDto = new UserBudgetSettingsDto(1L, 29, new BigDecimal("1000"));

        mockMvc.perform(post("/api/user-settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSettings_ShouldReturnUpdated() throws Exception {
        when(userBudgetSettingsService.updateSettings(eq(1L), any(UserBudgetSettings.class))).thenReturn(settings);

        mockMvc.perform(put("/api/user-settings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.monthlySalary").value(1000));
    }

    @Test
    void updateSettings_WhenNegativeSalary_ShouldReturnBadRequest() throws Exception {
        when(userBudgetSettingsService.updateSettings(eq(1L), any(UserBudgetSettings.class)))
                .thenThrow(new IllegalArgumentException("monthlySalary must be positive"));
        UserBudgetSettingsDto invalidDto = new UserBudgetSettingsDto(1L, 5, new BigDecimal("-100"));

        mockMvc.perform(put("/api/user-settings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteSettings_ShouldReturnNoContent() throws Exception {
        doNothing().when(userBudgetSettingsService).deleteSettings(1L);

        mockMvc.perform(delete("/api/user-settings/1"))
                .andExpect(status().isNoContent());
    }
}
