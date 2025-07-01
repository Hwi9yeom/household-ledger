package com.aurfebre.household.api;

import com.aurfebre.household.domain.Budget;
import com.aurfebre.household.domain.enums.BudgetPeriod;
import com.aurfebre.household.service.BudgetService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    public List<Budget> getAllBudgets() {
        return budgetService.getAllBudgets();
    }

    @GetMapping("/user/{userId}")
    public List<Budget> getBudgetsByUserId(@PathVariable Long userId) {
        return budgetService.getBudgetsByUserId(userId);
    }

    @GetMapping("/user/{userId}/period/{period}")
    public List<Budget> getBudgetsByUserIdAndPeriod(
            @PathVariable Long userId, 
            @PathVariable BudgetPeriod period) {
        return budgetService.getBudgetsByUserIdAndPeriod(userId, period);
    }

    @GetMapping("/user/{userId}/category/{categoryId}")
    public ResponseEntity<Budget> getBudgetByUserIdAndCategory(
            @PathVariable Long userId, 
            @PathVariable Long categoryId) {
        return budgetService.getBudgetByUserIdAndCategory(userId, categoryId)
                .map(budget -> ResponseEntity.ok().body(budget))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}/active")
    public List<Budget> getActiveBudgetsForDate(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        return budgetService.getActiveBudgetsForDate(userId, targetDate);
    }

    @GetMapping("/user/{userId}/category/{categoryId}/active")
    public ResponseEntity<Budget> getActiveBudgetForCategoryAndDate(
            @PathVariable Long userId,
            @PathVariable Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        return budgetService.getActiveBudgetForCategoryAndDate(userId, categoryId, targetDate)
                .map(budget -> ResponseEntity.ok().body(budget))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long id) {
        return budgetService.getBudgetById(id)
                .map(budget -> ResponseEntity.ok().body(budget))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Budget createBudget(@RequestBody Budget budget) {
        return budgetService.createBudget(budget);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Budget> updateBudget(@PathVariable Long id, @RequestBody Budget budgetDetails) {
        try {
            Budget updatedBudget = budgetService.updateBudget(id, budgetDetails);
            return ResponseEntity.ok(updatedBudget);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBudget(@PathVariable Long id) {
        try {
            budgetService.deleteBudget(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}/hard")
    public ResponseEntity<?> hardDeleteBudget(@PathVariable Long id) {
        try {
            budgetService.hardDeleteBudget(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activateBudget(@PathVariable Long id) {
        try {
            budgetService.activateBudget(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}