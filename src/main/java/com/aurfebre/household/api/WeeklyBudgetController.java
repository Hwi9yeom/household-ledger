package com.aurfebre.household.api;

import com.aurfebre.household.dto.WeeklyBudgetResponse;
import com.aurfebre.household.service.FinancialGoalService;
import com.aurfebre.household.service.UserBudgetSettingsService;
import com.aurfebre.household.service.WeeklyBudgetService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/weekly-budget")
public class WeeklyBudgetController {

    private final WeeklyBudgetService weeklyBudgetService;
    private final UserBudgetSettingsService settingsService;
    private final FinancialGoalService financialGoalService;

    public WeeklyBudgetController(WeeklyBudgetService weeklyBudgetService,
                                  UserBudgetSettingsService settingsService,
                                  FinancialGoalService financialGoalService) {
        this.weeklyBudgetService = weeklyBudgetService;
        this.settingsService = settingsService;
        this.financialGoalService = financialGoalService;
    }

    @GetMapping("/user/{userId}")
    public WeeklyBudgetResponse getWeeklyBudget(@PathVariable Long userId,
                                                @RequestParam(required = false)
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate currentWeek) {
        LocalDate week = currentWeek != null ? currentWeek : LocalDate.now();
        var settings = settingsService.getSettings(userId)
                .orElseThrow(() -> new IllegalArgumentException("Settings not found for user: " + userId));
        var summary = weeklyBudgetService.calculateWeeklyBudget(userId, settings.getAnnualSalary(), settings.getWeekStartDay(), week);
        var goals = financialGoalService.getGoalsForWeek(userId, settings.getWeekStartDay(), week);
        return new WeeklyBudgetResponse(summary.getWeeklyBudget(), summary.getWeeklySpending(),
                summary.getStartDate(), summary.getEndDate(), goals);
    }
}
