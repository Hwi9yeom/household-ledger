package com.aurfebre.household.service;

import com.aurfebre.household.domain.UserBudgetSettings;
import com.aurfebre.household.domain.enums.CycleType;
import com.aurfebre.household.repository.UserBudgetSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserBudgetSettingsService {

    private final UserBudgetSettingsRepository userBudgetSettingsRepository;

    public UserBudgetSettingsService(UserBudgetSettingsRepository userBudgetSettingsRepository) {
        this.userBudgetSettingsRepository = userBudgetSettingsRepository;
    }

    @Transactional(readOnly = true)
    public List<UserBudgetSettings> getAllSettings() {
        return userBudgetSettingsRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<UserBudgetSettings> getSettingsById(Long id) {
        return userBudgetSettingsRepository.findById(id);
    }

    public UserBudgetSettings createSettings(UserBudgetSettings settings) {
        return userBudgetSettingsRepository.save(settings);
    }

    public UserBudgetSettings updateSettings(Long id, UserBudgetSettings settingsDetails) {
        UserBudgetSettings settings = userBudgetSettingsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserBudgetSettings not found with id: " + id));

        settings.setMonthStartDay(settingsDetails.getMonthStartDay());
        settings.setCycleType(settingsDetails.getCycleType());
        settings.setWeekStartDay(settingsDetails.getWeekStartDay());

        return userBudgetSettingsRepository.save(settings);
    }

    public void deleteSettings(Long id) {
        userBudgetSettingsRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public UserBudgetSettings getSettings(Long userId) {
        return userBudgetSettingsRepository.findByUserId(userId)
                .orElseGet(() -> getDefaultSettings(userId));
    }

    private UserBudgetSettings getDefaultSettings(Long userId) {
        return new UserBudgetSettings(userId, 1, CycleType.MONTHLY, DayOfWeek.MONDAY);
    }
}
