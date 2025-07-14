package com.aurfebre.household.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "income_projections")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeProjection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "monthly_income", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "projected_annual_income", nullable = false, precision = 10, scale = 2)
    private BigDecimal projectedAnnualIncome;

    @Column(name = "start_month", nullable = false)
    private Integer startMonth;

    @Column(name = "end_month", nullable = false)
    private Integer endMonth;

    @Column(name = "income_type")
    @Enumerated(EnumType.STRING)
    private IncomeType incomeType;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum IncomeType {
        SALARY,          // 급여
        FREELANCE,       // 프리랜서
        BUSINESS,        // 사업
        INVESTMENT,      // 투자
        RENTAL,          // 임대
        OTHER            // 기타
    }
}