package com.aurfebre.household.repository;

import com.aurfebre.household.domain.Budget;
import com.aurfebre.household.domain.enums.BudgetPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    
    List<Budget> findByUserIdAndIsActiveTrueOrderByStartDateDesc(Long userId);
    
    List<Budget> findByUserIdAndPeriodAndIsActiveTrueOrderByStartDateDesc(Long userId, BudgetPeriod period);
    
    Optional<Budget> findByUserIdAndCategoryIdAndIsActiveTrue(Long userId, Long categoryId);
    
    @Query("SELECT b FROM Budget b WHERE b.userId = :userId AND b.isActive = true AND b.startDate <= :date AND b.endDate >= :date")
    List<Budget> findActiveBudgetsForDate(@Param("userId") Long userId, @Param("date") LocalDate date);
    
    @Query("SELECT b FROM Budget b WHERE b.userId = :userId AND b.categoryId = :categoryId AND b.isActive = true AND b.startDate <= :date AND b.endDate >= :date")
    Optional<Budget> findActiveBudgetForCategoryAndDate(
        @Param("userId") Long userId, 
        @Param("categoryId") Long categoryId, 
        @Param("date") LocalDate date);
    
    boolean existsByUserIdAndCategoryIdAndPeriodAndIsActiveTrue(Long userId, Long categoryId, BudgetPeriod period);
}