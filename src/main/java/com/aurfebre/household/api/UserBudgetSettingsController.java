package com.aurfebre.household.api;

import com.aurfebre.household.domain.UserBudgetSettings;
import com.aurfebre.household.service.UserBudgetSettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.DayOfWeek;

@RestController
@RequestMapping("/api/user-budget-settings")
public class UserBudgetSettingsController {

    private final UserBudgetSettingsService service;

    public UserBudgetSettingsController(UserBudgetSettingsService service) {
        this.service = service;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserBudgetSettings> getSettings(@PathVariable Long userId) {
        return service.getSettings(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/user/{userId}")
    public UserBudgetSettings updateSettings(@PathVariable Long userId, @RequestBody SettingsRequest request) {
        return service.updateSettings(userId, request.getAnnualSalary(), request.getWeekStartDay());
    }

    public static class SettingsRequest {
        private BigDecimal annualSalary;
        private DayOfWeek weekStartDay;

        public BigDecimal getAnnualSalary() {
            return annualSalary;
        }

        public void setAnnualSalary(BigDecimal annualSalary) {
            this.annualSalary = annualSalary;
        }

        public DayOfWeek getWeekStartDay() {
            return weekStartDay;
        }

        public void setWeekStartDay(DayOfWeek weekStartDay) {
            this.weekStartDay = weekStartDay;
        }
    }
}
