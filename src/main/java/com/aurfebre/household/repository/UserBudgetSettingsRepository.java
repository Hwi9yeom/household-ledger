package com.aurfebre.household.repository;

import com.aurfebre.household.domain.UserBudgetSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserBudgetSettingsRepository extends JpaRepository<UserBudgetSettings, Long> {
    Optional<UserBudgetSettings> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
