package com.aurfebre.household.repository;

import com.aurfebre.household.domain.RecurringTransaction;
import com.aurfebre.household.domain.enums.Frequency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {
    
    List<RecurringTransaction> findByUserIdAndIsActiveTrueOrderByNextScheduledDateAsc(Long userId);
    
    List<RecurringTransaction> findByUserIdAndFrequencyAndIsActiveTrueOrderByNextScheduledDateAsc(Long userId, Frequency frequency);
    
    List<RecurringTransaction> findByUserIdAndCategoryIdAndIsActiveTrueOrderByNextScheduledDateAsc(Long userId, Long categoryId);
    
    @Query("SELECT r FROM RecurringTransaction r WHERE r.isActive = true AND r.nextScheduledDate <= :date")
    List<RecurringTransaction> findDueTransactions(@Param("date") LocalDate date);
    
    @Query("SELECT r FROM RecurringTransaction r WHERE r.userId = :userId AND r.isActive = true AND r.nextScheduledDate <= :date")
    List<RecurringTransaction> findDueTransactionsByUser(@Param("userId") Long userId, @Param("date") LocalDate date);
}