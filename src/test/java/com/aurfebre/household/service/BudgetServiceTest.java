package com.aurfebre.household.service;

import com.aurfebre.household.domain.Budget;
import com.aurfebre.household.domain.enums.BudgetPeriod;
import com.aurfebre.household.repository.BudgetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @InjectMocks
    private BudgetService budgetService;

    private Budget testBudget;

    @BeforeEach
    void setUp() {
        testBudget = new Budget(
            1L, 
            1L, 
            BudgetPeriod.MONTHLY, 
            new BigDecimal("500000"), 
            LocalDate.of(2025, 7, 1), 
            LocalDate.of(2025, 7, 31)
        );
        testBudget.setId(1L);
        testBudget.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getAllBudgets_ShouldReturnAllBudgets() {
        // Given
        List<Budget> budgets = Arrays.asList(
            testBudget,
            new Budget(1L, 2L, BudgetPeriod.WEEKLY, new BigDecimal("100000"), 
                      LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 7))
        );
        when(budgetRepository.findAll()).thenReturn(budgets);

        // When
        List<Budget> result = budgetService.getAllBudgets();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).contains(testBudget);
        verify(budgetRepository).findAll();
    }

    @Test
    void getBudgetsByUserId_ShouldReturnActiveBudgets() {
        // Given
        List<Budget> budgets = Arrays.asList(testBudget);
        when(budgetRepository.findByUserIdAndIsActiveTrueOrderByStartDateDesc(1L)).thenReturn(budgets);

        // When
        List<Budget> result = budgetService.getBudgetsByUserId(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).contains(testBudget);
        verify(budgetRepository).findByUserIdAndIsActiveTrueOrderByStartDateDesc(1L);
    }

    @Test
    void getBudgetsByUserIdAndPeriod_ShouldReturnFilteredBudgets() {
        // Given
        List<Budget> budgets = Arrays.asList(testBudget);
        when(budgetRepository.findByUserIdAndPeriodAndIsActiveTrueOrderByStartDateDesc(1L, BudgetPeriod.MONTHLY))
            .thenReturn(budgets);

        // When
        List<Budget> result = budgetService.getBudgetsByUserIdAndPeriod(1L, BudgetPeriod.MONTHLY);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPeriod()).isEqualTo(BudgetPeriod.MONTHLY);
        verify(budgetRepository).findByUserIdAndPeriodAndIsActiveTrueOrderByStartDateDesc(1L, BudgetPeriod.MONTHLY);
    }

    @Test
    void getBudgetByUserIdAndCategory_ShouldReturnCategoryBudget() {
        // Given
        when(budgetRepository.findByUserIdAndCategoryIdAndIsActiveTrue(1L, 1L))
            .thenReturn(Optional.of(testBudget));

        // When
        Optional<Budget> result = budgetService.getBudgetByUserIdAndCategory(1L, 1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getCategoryId()).isEqualTo(1L);
        verify(budgetRepository).findByUserIdAndCategoryIdAndIsActiveTrue(1L, 1L);
    }

    @Test
    void getActiveBudgetsForDate_ShouldReturnActiveBudgets() {
        // Given
        LocalDate testDate = LocalDate.of(2025, 7, 15);
        List<Budget> budgets = Arrays.asList(testBudget);
        when(budgetRepository.findActiveBudgetsForDate(1L, testDate)).thenReturn(budgets);

        // When
        List<Budget> result = budgetService.getActiveBudgetsForDate(1L, testDate);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).contains(testBudget);
        verify(budgetRepository).findActiveBudgetsForDate(1L, testDate);
    }

    @Test
    void getActiveBudgetForCategoryAndDate_ShouldReturnBudget() {
        // Given
        LocalDate testDate = LocalDate.of(2025, 7, 15);
        when(budgetRepository.findActiveBudgetForCategoryAndDate(1L, 1L, testDate))
            .thenReturn(Optional.of(testBudget));

        // When
        Optional<Budget> result = budgetService.getActiveBudgetForCategoryAndDate(1L, 1L, testDate);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testBudget);
        verify(budgetRepository).findActiveBudgetForCategoryAndDate(1L, 1L, testDate);
    }

    @Test
    void getBudgetById_WhenBudgetExists_ShouldReturnBudget() {
        // Given
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(testBudget));

        // When
        Optional<Budget> result = budgetService.getBudgetById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testBudget);
        verify(budgetRepository).findById(1L);
    }

    @Test
    void createBudget_WhenValidBudget_ShouldCreateBudget() {
        // Given
        Budget newBudget = new Budget(
            1L, 2L, BudgetPeriod.WEEKLY, new BigDecimal("100000"), 
            LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 7)
        );
        when(budgetRepository.existsByUserIdAndCategoryIdAndPeriodAndIsActiveTrue(1L, 2L, BudgetPeriod.WEEKLY))
            .thenReturn(false);
        when(budgetRepository.save(any(Budget.class))).thenReturn(newBudget);

        // When
        Budget result = budgetService.createBudget(newBudget);

        // Then
        assertThat(result).isEqualTo(newBudget);
        verify(budgetRepository).existsByUserIdAndCategoryIdAndPeriodAndIsActiveTrue(1L, 2L, BudgetPeriod.WEEKLY);
        verify(budgetRepository).save(newBudget);
    }

    @Test
    void createBudget_WhenAmountIsZero_ShouldThrowException() {
        // Given
        Budget invalidBudget = new Budget(
            1L, 1L, BudgetPeriod.MONTHLY, BigDecimal.ZERO, 
            LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 31)
        );

        // When & Then
        assertThatThrownBy(() -> budgetService.createBudget(invalidBudget))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Budget amount must be greater than zero");
        
        verify(budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    void createBudget_WhenAmountIsNegative_ShouldThrowException() {
        // Given
        Budget invalidBudget = new Budget(
            1L, 1L, BudgetPeriod.MONTHLY, new BigDecimal("-100"), 
            LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 31)
        );

        // When & Then
        assertThatThrownBy(() -> budgetService.createBudget(invalidBudget))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Budget amount must be greater than zero");
        
        verify(budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    void createBudget_WhenEndDateBeforeStartDate_ShouldThrowException() {
        // Given
        Budget invalidBudget = new Budget(
            1L, 1L, BudgetPeriod.MONTHLY, new BigDecimal("500000"), 
            LocalDate.of(2025, 7, 31), LocalDate.of(2025, 7, 1)
        );

        // When & Then
        assertThatThrownBy(() -> budgetService.createBudget(invalidBudget))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("End date must be after start date");
        
        verify(budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    void createBudget_WhenActiveBudgetExists_ShouldThrowException() {
        // Given
        Budget duplicateBudget = new Budget(
            1L, 1L, BudgetPeriod.MONTHLY, new BigDecimal("500000"), 
            LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 31)
        );
        when(budgetRepository.existsByUserIdAndCategoryIdAndPeriodAndIsActiveTrue(1L, 1L, BudgetPeriod.MONTHLY))
            .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> budgetService.createBudget(duplicateBudget))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Active budget already exists for this category and period");
        
        verify(budgetRepository).existsByUserIdAndCategoryIdAndPeriodAndIsActiveTrue(1L, 1L, BudgetPeriod.MONTHLY);
        verify(budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    void updateBudget_WhenValidUpdate_ShouldUpdateBudget() {
        // Given
        Budget updateDetails = new Budget(
            1L, 2L, BudgetPeriod.WEEKLY, new BigDecimal("300000"), 
            LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 7)
        );
        
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(testBudget));
        when(budgetRepository.save(any(Budget.class))).thenReturn(testBudget);

        // When
        Budget result = budgetService.updateBudget(1L, updateDetails);

        // Then
        assertThat(result.getCategoryId()).isEqualTo(2L);
        assertThat(result.getPeriod()).isEqualTo(BudgetPeriod.WEEKLY);
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("300000"));
        verify(budgetRepository).findById(1L);
        verify(budgetRepository).save(testBudget);
    }

    @Test
    void updateBudget_WhenBudgetNotExists_ShouldThrowException() {
        // Given
        Budget updateDetails = new Budget(
            1L, 2L, BudgetPeriod.WEEKLY, new BigDecimal("300000"), 
            LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 7)
        );
        when(budgetRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> budgetService.updateBudget(1L, updateDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Budget not found with id: 1");
        
        verify(budgetRepository).findById(1L);
        verify(budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    void updateBudget_WhenAmountIsZero_ShouldThrowException() {
        // Given
        Budget updateDetails = new Budget(
            1L, 2L, BudgetPeriod.WEEKLY, BigDecimal.ZERO, 
            LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 7)
        );
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(testBudget));

        // When & Then
        assertThatThrownBy(() -> budgetService.updateBudget(1L, updateDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Budget amount must be greater than zero");
        
        verify(budgetRepository).findById(1L);
        verify(budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    void deleteBudget_WhenBudgetExists_ShouldDeactivateBudget() {
        // Given
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(testBudget));
        when(budgetRepository.save(any(Budget.class))).thenReturn(testBudget);

        // When
        budgetService.deleteBudget(1L);

        // Then
        assertThat(testBudget.getIsActive()).isFalse();
        verify(budgetRepository).findById(1L);
        verify(budgetRepository).save(testBudget);
    }

    @Test
    void deleteBudget_WhenBudgetNotExists_ShouldThrowException() {
        // Given
        when(budgetRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> budgetService.deleteBudget(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Budget not found with id: 1");
        
        verify(budgetRepository).findById(1L);
        verify(budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    void hardDeleteBudget_WhenBudgetExists_ShouldDeletePermanently() {
        // Given
        when(budgetRepository.existsById(1L)).thenReturn(true);

        // When
        budgetService.hardDeleteBudget(1L);

        // Then
        verify(budgetRepository).existsById(1L);
        verify(budgetRepository).deleteById(1L);
    }

    @Test
    void hardDeleteBudget_WhenBudgetNotExists_ShouldThrowException() {
        // Given
        when(budgetRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> budgetService.hardDeleteBudget(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Budget not found with id: 1");
        
        verify(budgetRepository).existsById(1L);
        verify(budgetRepository, never()).deleteById(any());
    }

    @Test
    void activateBudget_WhenBudgetExists_ShouldActivateBudget() {
        // Given
        testBudget.setIsActive(false);
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(testBudget));
        when(budgetRepository.save(any(Budget.class))).thenReturn(testBudget);

        // When
        budgetService.activateBudget(1L);

        // Then
        assertThat(testBudget.getIsActive()).isTrue();
        verify(budgetRepository).findById(1L);
        verify(budgetRepository).save(testBudget);
    }

    @Test
    void activateBudget_WhenBudgetNotExists_ShouldThrowException() {
        // Given
        when(budgetRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> budgetService.activateBudget(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Budget not found with id: 1");
        
        verify(budgetRepository).findById(1L);
        verify(budgetRepository, never()).save(any(Budget.class));
    }
}