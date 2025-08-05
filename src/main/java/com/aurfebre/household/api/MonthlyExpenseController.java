package com.aurfebre.household.api;

import com.aurfebre.household.domain.LedgerEntry;
import com.aurfebre.household.dto.MonthlyExpenseComparison;
import com.aurfebre.household.dto.MonthlyExpenseSummary;
import com.aurfebre.household.service.MonthlyExpenseService;
import com.aurfebre.household.service.UserBudgetSettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/monthly-expenses")
public class MonthlyExpenseController {

    private final MonthlyExpenseService monthlyExpenseService;
    private final UserBudgetSettingsService userBudgetSettingsService;

    public MonthlyExpenseController(MonthlyExpenseService monthlyExpenseService,
                                    UserBudgetSettingsService userBudgetSettingsService) {
        this.monthlyExpenseService = monthlyExpenseService;
        this.userBudgetSettingsService = userBudgetSettingsService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LedgerEntry>> getMonthlyExpenses(
            @PathVariable Long userId,
            @RequestParam YearMonth yearMonth) {
        
        int monthStartDay = userBudgetSettingsService.getSettings(userId).getMonthStartDay();
        List<LedgerEntry> expenses = monthlyExpenseService.getMonthlyExpenses(userId, yearMonth, monthStartDay);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<MonthlyExpenseSummary> getMonthlyExpensesSummary(
            @PathVariable Long userId,
            @RequestParam YearMonth yearMonth) {
        
        int monthStartDay = userBudgetSettingsService.getSettings(userId).getMonthStartDay();
        MonthlyExpenseSummary summary = monthlyExpenseService.getMonthlyExpensesSummary(userId, yearMonth, monthStartDay);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/user/{userId}/comparison")
    public ResponseEntity<MonthlyExpenseComparison> getMonthlyExpensesComparison(
            @PathVariable Long userId,
            @RequestParam YearMonth yearMonth) {
        
        int monthStartDay = userBudgetSettingsService.getSettings(userId).getMonthStartDay();
        MonthlyExpenseComparison comparison = monthlyExpenseService.getMonthlyComparison(userId, yearMonth, monthStartDay);
        return ResponseEntity.ok(comparison);
    }
}