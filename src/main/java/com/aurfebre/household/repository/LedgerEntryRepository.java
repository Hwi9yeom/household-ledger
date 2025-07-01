package com.aurfebre.household.repository;

import com.aurfebre.household.domain.LedgerEntry;
import com.aurfebre.household.domain.enums.EntryType;
import com.aurfebre.household.domain.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
    
    List<LedgerEntry> findByUserIdOrderByDateDesc(Long userId);
    
    List<LedgerEntry> findByUserIdAndEntryTypeOrderByDateDesc(Long userId, EntryType entryType);
    
    List<LedgerEntry> findByUserIdAndCategoryIdOrderByDateDesc(Long userId, Long categoryId);
    
    List<LedgerEntry> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate);
    
    List<LedgerEntry> findByUserIdAndEntryTypeAndDateBetweenOrderByDateDesc(
        Long userId, EntryType entryType, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(l.amount) FROM LedgerEntry l WHERE l.userId = :userId AND l.entryType = :entryType AND l.date BETWEEN :startDate AND :endDate")
    BigDecimal sumByUserIdAndEntryTypeAndDateBetween(
        @Param("userId") Long userId, 
        @Param("entryType") EntryType entryType, 
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(l.amount) FROM LedgerEntry l WHERE l.userId = :userId AND l.categoryId = :categoryId AND l.date BETWEEN :startDate AND :endDate")
    BigDecimal sumByUserIdAndCategoryIdAndDateBetween(
        @Param("userId") Long userId, 
        @Param("categoryId") Long categoryId, 
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate);
}