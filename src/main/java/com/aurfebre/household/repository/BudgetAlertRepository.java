package com.aurfebre.household.repository;

import com.aurfebre.household.domain.BudgetAlert;
import com.aurfebre.household.domain.enums.AlertType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetAlertRepository extends JpaRepository<BudgetAlert, Long> {
    
    List<BudgetAlert> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<BudgetAlert> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
    
    List<BudgetAlert> findByUserIdAndAlertTypeOrderByCreatedAtDesc(Long userId, AlertType alertType);
    
    List<BudgetAlert> findByBudgetIdOrderByCreatedAtDesc(Long budgetId);
    
    @Modifying
    @Query("UPDATE BudgetAlert b SET b.isRead = true WHERE b.userId = :userId")
    void markAllAsReadByUserId(@Param("userId") Long userId);
    
    @Modifying
    @Query("UPDATE BudgetAlert b SET b.isRead = true WHERE b.budgetId = :budgetId")
    void markAllAsReadByBudgetId(@Param("budgetId") Long budgetId);
    
    long countByUserIdAndIsReadFalse(Long userId);
}