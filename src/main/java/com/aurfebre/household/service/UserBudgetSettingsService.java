package com.aurfebre.household.service;

import com.aurfebre.household.domain.UserBudgetSettings;
import com.aurfebre.household.repository.UserBudgetSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.Optional;

@Service
@Transactional
public class UserBudgetSettingsService {

    private final UserBudgetSettingsRepository repository;

    public UserBudgetSettingsService(UserBudgetSettingsRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Optional<UserBudgetSettings> getSettings(Long userId) {
        return repository.findByUserId(userId);
    }

    public UserBudgetSettings updateSettings(Long userId, BigDecimal annualSalary, DayOfWeek weekStartDay) {
        UserBudgetSettings settings = repository.findByUserId(userId)
                .orElse(new UserBudgetSettings(userId, annualSalary, weekStartDay));
        settings.setAnnualSalary(annualSalary);
        settings.setWeekStartDay(weekStartDay);
        return repository.save(settings);
    }
}
