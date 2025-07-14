package com.aurfebre.household.dto;

import com.aurfebre.household.domain.IncomeProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeProjectionResponse {

    private Long id;
    private Long userId;
    private BigDecimal monthlyIncome;
    private Integer year;
    private BigDecimal projectedAnnualIncome;
    private Integer startMonth;
    private Integer endMonth;
    private IncomeProjection.IncomeType incomeType;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static IncomeProjectionResponse from(IncomeProjection projection) {
        return IncomeProjectionResponse.builder()
                .id(projection.getId())
                .userId(projection.getUserId())
                .monthlyIncome(projection.getMonthlyIncome())
                .year(projection.getYear())
                .projectedAnnualIncome(projection.getProjectedAnnualIncome())
                .startMonth(projection.getStartMonth())
                .endMonth(projection.getEndMonth())
                .incomeType(projection.getIncomeType())
                .description(projection.getDescription())
                .isActive(projection.getIsActive())
                .createdAt(projection.getCreatedAt())
                .updatedAt(projection.getUpdatedAt())
                .build();
    }
}