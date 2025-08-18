package com.aurfebre.household.api;

import com.aurfebre.household.domain.FinancialGoal;
import com.aurfebre.household.service.FinancialGoalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/financial-goals")
public class FinancialGoalController {

    private final FinancialGoalService financialGoalService;

    public FinancialGoalController(FinancialGoalService financialGoalService) {
        this.financialGoalService = financialGoalService;
    }

    @GetMapping("/user/{userId}")
    public List<FinancialGoal> getGoalsByUserId(@PathVariable Long userId) {
        return financialGoalService.getGoalsByUserId(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FinancialGoal> getGoalById(@PathVariable Long id) {
        return financialGoalService.getGoalById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FinancialGoal createGoal(@RequestBody FinancialGoal goal) {
        return financialGoalService.createGoal(goal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FinancialGoal> updateGoal(@PathVariable Long id, @RequestBody FinancialGoal goalDetails) {
        try {
            FinancialGoal updated = financialGoalService.updateGoal(id, goalDetails);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoal(@PathVariable Long id) {
        try {
            financialGoalService.deleteGoal(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}/hard")
    public ResponseEntity<?> hardDeleteGoal(@PathVariable Long id) {
        try {
            financialGoalService.hardDeleteGoal(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<BigDecimal> getProgress(@PathVariable Long id) {
        try {
            BigDecimal progress = financialGoalService.getProgress(id);
            return ResponseEntity.ok(progress);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/apply-contribution")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void applyMonthlyContribution() {
        financialGoalService.applyMonthlyContribution();
    }
}

