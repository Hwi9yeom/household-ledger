package com.aurfebre.household.service;

import com.aurfebre.household.domain.UserBudgetSettings;
import com.aurfebre.household.repository.UserBudgetSettingsRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserBudgetSettingsService {

    private final UserBudgetSettingsRepository repository;

    public UserBudgetSettingsService(UserBudgetSettingsRepository repository) {
        this.repository = repository;
    }

    public Optional<UserBudgetSettings> getSettingsByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    public UserBudgetSettings createSettings(UserBudgetSettings settings) {
        validate(settings);
        return repository.save(settings);
    }

    public UserBudgetSettings updateSettings(Long userId, UserBudgetSettings settings) {
        validate(settings);
        UserBudgetSettings existing = repository.findByUserId(userId)
                .orElseThrow(NoSuchElementException::new);
        existing.setMonthStartDay(settings.getMonthStartDay());
        existing.setMonthlySalary(settings.getMonthlySalary());
        return repository.save(existing);
    }

    public void deleteSettings(Long userId) {
        UserBudgetSettings existing = repository.findByUserId(userId)
                .orElseThrow(NoSuchElementException::new);
        repository.delete(existing);
    }

    private void validate(UserBudgetSettings settings) {
        if (settings.getMonthStartDay() != null && settings.getMonthStartDay() > 28) {
            throw new IllegalArgumentException("monthStartDay must be 28 or less");
        }
        BigDecimal salary = settings.getMonthlySalary();
        if (salary != null && salary.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("monthlySalary must be positive");
        }
    }
}
