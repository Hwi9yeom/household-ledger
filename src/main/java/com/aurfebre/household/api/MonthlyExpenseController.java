package com.aurfebre.household.api;

import com.aurfebre.household.domain.LedgerEntry;
import com.aurfebre.household.dto.MonthlyExpenseComparison;
import com.aurfebre.household.dto.MonthlyExpenseSummary;
import com.aurfebre.household.service.MonthlyExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/monthly-expenses")
public class MonthlyExpenseController {

    private final MonthlyExpenseService monthlyExpenseService;

    public MonthlyExpenseController(MonthlyExpenseService monthlyExpenseService) {
        this.monthlyExpenseService = monthlyExpenseService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LedgerEntry>> getMonthlyExpenses(
            @PathVariable Long userId,
            @RequestParam YearMonth yearMonth) {
        
        List<LedgerEntry> expenses = monthlyExpenseService.getMonthlyExpenses(userId, yearMonth);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<MonthlyExpenseSummary> getMonthlyExpensesSummary(
            @PathVariable Long userId,
            @RequestParam YearMonth yearMonth) {
        
        MonthlyExpenseSummary summary = monthlyExpenseService.getMonthlyExpensesSummary(userId, yearMonth);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/user/{userId}/comparison")
    public ResponseEntity<MonthlyExpenseComparison> getMonthlyExpensesComparison(
            @PathVariable Long userId,
            @RequestParam YearMonth yearMonth) {
        
        MonthlyExpenseComparison comparison = monthlyExpenseService.getMonthlyComparison(userId, yearMonth);
        return ResponseEntity.ok(comparison);
    }
}