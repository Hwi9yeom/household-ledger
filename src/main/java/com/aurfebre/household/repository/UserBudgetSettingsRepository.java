package com.aurfebre.household.repository;

import com.aurfebre.household.domain.UserBudgetSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserBudgetSettingsRepository extends JpaRepository<UserBudgetSettings, Long> {
    Optional<UserBudgetSettings> findByUserId(Long userId);
}
