package com.aurfebre.household.service;

import com.aurfebre.household.domain.IncomeProjection;
import com.aurfebre.household.dto.IncomeProjectionRequest;
import com.aurfebre.household.dto.IncomeProjectionResponse;
import com.aurfebre.household.repository.IncomeProjectionRepository;
import com.aurfebre.household.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class IncomeProjectionService {

    private final IncomeProjectionRepository incomeProjectionRepository;

    public IncomeProjectionResponse createProjection(IncomeProjectionRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new IllegalStateException("User must be authenticated");
        }

        // 연간 예상 수입 계산
        BigDecimal projectedAnnualIncome = calculateAnnualIncome(
                request.getMonthlyIncome(), 
                request.getStartMonth(), 
                request.getEndMonth()
        );

        IncomeProjection projection = IncomeProjection.builder()
                .userId(userId)
                .monthlyIncome(request.getMonthlyIncome())
                .year(request.getYear())
                .projectedAnnualIncome(projectedAnnualIncome)
                .startMonth(request.getStartMonth())
                .endMonth(request.getEndMonth())
                .incomeType(request.getIncomeType())
                .description(request.getDescription())
                .isActive(true)
                .build();

        IncomeProjection saved = incomeProjectionRepository.save(projection);
        log.info("Created income projection for user {} with annual income {}", userId, projectedAnnualIncome);
        
        return IncomeProjectionResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<IncomeProjectionResponse> getProjectionsByYear(Integer year) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new IllegalStateException("User must be authenticated");
        }

        return incomeProjectionRepository.findByUserIdAndYearAndIsActive(userId, year, true)
                .stream()
                .map(IncomeProjectionResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IncomeProjectionResponse> getAllActiveProjections() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new IllegalStateException("User must be authenticated");
        }

        return incomeProjectionRepository.findByUserIdAndIsActiveOrderByYearDescCreatedAtDesc(userId, true)
                .stream()
                .map(IncomeProjectionResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalProjectedIncomeForYear(Integer year) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new IllegalStateException("User must be authenticated");
        }

        return incomeProjectionRepository.sumProjectedIncomeByUserIdAndYear(userId, year)
                .map(BigDecimal::valueOf)
                .orElse(BigDecimal.ZERO);
    }

    public IncomeProjectionResponse updateProjection(Long id, IncomeProjectionRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new IllegalStateException("User must be authenticated");
        }

        IncomeProjection projection = incomeProjectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Income projection not found"));

        if (!projection.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Access denied: You can only update your own projections");
        }

        // 연간 예상 수입 재계산
        BigDecimal projectedAnnualIncome = calculateAnnualIncome(
                request.getMonthlyIncome(), 
                request.getStartMonth(), 
                request.getEndMonth()
        );

        projection.setMonthlyIncome(request.getMonthlyIncome());
        projection.setYear(request.getYear());
        projection.setProjectedAnnualIncome(projectedAnnualIncome);
        projection.setStartMonth(request.getStartMonth());
        projection.setEndMonth(request.getEndMonth());
        projection.setIncomeType(request.getIncomeType());
        projection.setDescription(request.getDescription());

        IncomeProjection updated = incomeProjectionRepository.save(projection);
        log.info("Updated income projection {} for user {}", id, userId);
        
        return IncomeProjectionResponse.from(updated);
    }

    public void deactivateProjection(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new IllegalStateException("User must be authenticated");
        }

        IncomeProjection projection = incomeProjectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Income projection not found"));

        if (!projection.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Access denied: You can only deactivate your own projections");
        }

        projection.setIsActive(false);
        incomeProjectionRepository.save(projection);
        log.info("Deactivated income projection {} for user {}", id, userId);
    }

    public IncomeProjectionResponse createQuickProjection(BigDecimal monthlyIncome, IncomeProjection.IncomeType incomeType) {
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        IncomeProjectionRequest request = IncomeProjectionRequest.builder()
                .monthlyIncome(monthlyIncome)
                .year(currentYear)
                .startMonth(currentMonth)
                .endMonth(12)
                .incomeType(incomeType)
                .description("Quick projection from " + now.getMonth().name() + " " + currentYear)
                .build();

        return createProjection(request);
    }

    @Transactional(readOnly = true)
    public List<IncomeProjectionResponse> getProjectionsByYearRange(Integer startYear, Integer endYear) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new IllegalStateException("User must be authenticated");
        }

        return incomeProjectionRepository.findByUserIdAndYearRange(userId, startYear, endYear)
                .stream()
                .map(IncomeProjectionResponse::from)
                .collect(Collectors.toList());
    }

    private BigDecimal calculateAnnualIncome(BigDecimal monthlyIncome, Integer startMonth, Integer endMonth) {
        int monthCount = endMonth - startMonth + 1;
        return monthlyIncome.multiply(BigDecimal.valueOf(monthCount));
    }
}