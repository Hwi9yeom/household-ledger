package com.aurfebre.household.api;

import com.aurfebre.household.domain.IncomeProjection;
import com.aurfebre.household.dto.IncomeProjectionRequest;
import com.aurfebre.household.dto.IncomeProjectionResponse;
import com.aurfebre.household.service.IncomeProjectionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/income-projections")
@RequiredArgsConstructor
public class IncomeProjectionController {

    private final IncomeProjectionService incomeProjectionService;

    @PostMapping
    public ResponseEntity<IncomeProjectionResponse> createProjection(@Valid @RequestBody IncomeProjectionRequest request) {
        IncomeProjectionResponse response = incomeProjectionService.createProjection(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/quick")
    public ResponseEntity<IncomeProjectionResponse> createQuickProjection(
            @RequestParam @NotNull BigDecimal monthlyIncome,
            @RequestParam(defaultValue = "SALARY") IncomeProjection.IncomeType incomeType) {
        IncomeProjectionResponse response = incomeProjectionService.createQuickProjection(monthlyIncome, incomeType);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<IncomeProjectionResponse>> getAllActiveProjections() {
        List<IncomeProjectionResponse> projections = incomeProjectionService.getAllActiveProjections();
        return ResponseEntity.ok(projections);
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<List<IncomeProjectionResponse>> getProjectionsByYear(
            @PathVariable @Min(2020) @Max(2100) Integer year) {
        List<IncomeProjectionResponse> projections = incomeProjectionService.getProjectionsByYear(year);
        return ResponseEntity.ok(projections);
    }

    @GetMapping("/year/{year}/total")
    public ResponseEntity<Map<String, Object>> getTotalProjectedIncomeForYear(
            @PathVariable @Min(2020) @Max(2100) Integer year) {
        BigDecimal total = incomeProjectionService.getTotalProjectedIncomeForYear(year);
        return ResponseEntity.ok(Map.of(
                "year", year,
                "totalProjectedIncome", total
        ));
    }

    @GetMapping("/range")
    public ResponseEntity<List<IncomeProjectionResponse>> getProjectionsByYearRange(
            @RequestParam @Min(2020) @Max(2100) Integer startYear,
            @RequestParam @Min(2020) @Max(2100) Integer endYear) {
        if (startYear > endYear) {
            return ResponseEntity.badRequest().build();
        }
        List<IncomeProjectionResponse> projections = incomeProjectionService.getProjectionsByYearRange(startYear, endYear);
        return ResponseEntity.ok(projections);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncomeProjectionResponse> updateProjection(
            @PathVariable Long id,
            @Valid @RequestBody IncomeProjectionRequest request) {
        IncomeProjectionResponse response = incomeProjectionService.updateProjection(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateProjection(@PathVariable Long id) {
        incomeProjectionService.deactivateProjection(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/calculate")
    public ResponseEntity<Map<String, Object>> calculateAnnualIncome(
            @RequestParam @NotNull BigDecimal monthlyIncome,
            @RequestParam(defaultValue = "1") @Min(1) @Max(12) Integer startMonth,
            @RequestParam(defaultValue = "12") @Min(1) @Max(12) Integer endMonth) {
        if (startMonth > endMonth) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Start month must be less than or equal to end month"
            ));
        }
        
        int monthCount = endMonth - startMonth + 1;
        BigDecimal annualIncome = monthlyIncome.multiply(BigDecimal.valueOf(monthCount));
        
        return ResponseEntity.ok(Map.of(
                "monthlyIncome", monthlyIncome,
                "startMonth", startMonth,
                "endMonth", endMonth,
                "monthCount", monthCount,
                "projectedAnnualIncome", annualIncome
        ));
    }
}