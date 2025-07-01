package com.aurfebre.household.repository;

import com.aurfebre.household.domain.FinancialGoal;
import com.aurfebre.household.domain.enums.GoalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FinancialGoalRepository extends JpaRepository<FinancialGoal, Long> {
    
    List<FinancialGoal> findByUserIdAndIsActiveTrueOrderByPriorityAsc(Long userId);
    
    List<FinancialGoal> findByUserIdAndGoalTypeAndIsActiveTrueOrderByPriorityAsc(Long userId, GoalType goalType);
    
    @Query("SELECT f FROM FinancialGoal f WHERE f.userId = :userId AND f.isActive = true AND f.targetDate <= :date ORDER BY f.priority ASC")
    List<FinancialGoal> findGoalsDueByDate(@Param("userId") Long userId, @Param("date") LocalDate date);
    
    @Query("SELECT f FROM FinancialGoal f WHERE f.userId = :userId AND f.isActive = true AND f.currentAmount >= f.targetAmount ORDER BY f.priority ASC")
    List<FinancialGoal> findCompletedGoals(@Param("userId") Long userId);
    
    boolean existsByUserIdAndPriorityAndIsActiveTrue(Long userId, Integer priority);
}