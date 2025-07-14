package com.aurfebre.household.dto;

import com.aurfebre.household.domain.IncomeProjection;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeProjectionRequest {

    @NotNull(message = "Monthly income is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Monthly income must be greater than 0")
    private BigDecimal monthlyIncome;

    @NotNull(message = "Year is required")
    @Min(value = 2020, message = "Year must be 2020 or later")
    @Max(value = 2100, message = "Year must be before 2100")
    private Integer year;

    @NotNull(message = "Start month is required")
    @Min(value = 1, message = "Start month must be between 1 and 12")
    @Max(value = 12, message = "Start month must be between 1 and 12")
    private Integer startMonth;

    @NotNull(message = "End month is required")
    @Min(value = 1, message = "End month must be between 1 and 12")
    @Max(value = 12, message = "End month must be between 1 and 12")
    private Integer endMonth;

    @NotNull(message = "Income type is required")
    private IncomeProjection.IncomeType incomeType;

    private String description;

    @AssertTrue(message = "End month must be greater than or equal to start month")
    public boolean isValidMonthRange() {
        return endMonth == null || startMonth == null || endMonth >= startMonth;
    }
}