package com.aurfebre.household.service;

import com.aurfebre.household.domain.FinancialGoal;
import com.aurfebre.household.domain.enums.GoalType;
import com.aurfebre.household.repository.FinancialGoalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinancialGoalServiceTest {

    @Mock
    private FinancialGoalRepository financialGoalRepository;

    @InjectMocks
    private FinancialGoalService financialGoalService;

    private FinancialGoal testGoal;

    @BeforeEach
    void setUp() {
        testGoal = new FinancialGoal(1L, GoalType.SAVING, "Emergency Fund",
            new BigDecimal("10000"), LocalDate.of(2025, 12, 31), 1);
        testGoal.setId(1L);
    }

    @Test
    void createGoal_ShouldSaveGoal() {
        when(financialGoalRepository.existsByUserIdAndPriorityAndIsActiveTrue(1L, 1)).thenReturn(false);
        when(financialGoalRepository.save(any(FinancialGoal.class))).thenReturn(testGoal);

        FinancialGoal result = financialGoalService.createGoal(testGoal);

        assertThat(result).isEqualTo(testGoal);
        verify(financialGoalRepository).save(testGoal);
    }

    @Test
    void updateGoal_ShouldUpdateGoal() {
        FinancialGoal updatedDetails = new FinancialGoal(1L, GoalType.SAVING, "Vacation",
            new BigDecimal("15000"), LocalDate.of(2026, 6, 30), 2);
        updatedDetails.setCurrentAmount(new BigDecimal("5000"));

        when(financialGoalRepository.findById(1L)).thenReturn(Optional.of(testGoal));
        when(financialGoalRepository.existsByUserIdAndPriorityAndIsActiveTrue(1L, 2)).thenReturn(false);
        when(financialGoalRepository.save(any(FinancialGoal.class))).thenReturn(updatedDetails);

        FinancialGoal result = financialGoalService.updateGoal(1L, updatedDetails);

        assertThat(result.getName()).isEqualTo("Vacation");
        assertThat(result.getPriority()).isEqualTo(2);
        verify(financialGoalRepository).save(testGoal);
    }

    @Test
    void deleteGoal_ShouldDeactivateGoal() {
        when(financialGoalRepository.findById(1L)).thenReturn(Optional.of(testGoal));

        financialGoalService.deleteGoal(1L);

        assertThat(testGoal.getIsActive()).isFalse();
        verify(financialGoalRepository).save(testGoal);
    }

    @Test
    void hardDeleteGoal_ShouldRemoveGoal() {
        when(financialGoalRepository.existsById(1L)).thenReturn(true);

        financialGoalService.hardDeleteGoal(1L);

        verify(financialGoalRepository).deleteById(1L);
    }

    @Test
    void getProgress_ShouldReturnCorrectPercentage() {
        testGoal.setCurrentAmount(new BigDecimal("2000"));
        when(financialGoalRepository.findById(1L)).thenReturn(Optional.of(testGoal));

        BigDecimal progress = financialGoalService.getProgress(1L);

        assertThat(progress).isEqualTo(new BigDecimal("20.0000"));
    }

    @Test
    void applyMonthlyContribution_ShouldIncreaseCurrentAmount() {
        testGoal.setMonthlyContribution(new BigDecimal("500"));
        List<FinancialGoal> goals = Arrays.asList(testGoal);
        when(financialGoalRepository.findAll()).thenReturn(goals);

        financialGoalService.applyMonthlyContribution();

        assertThat(testGoal.getCurrentAmount()).isEqualByComparingTo(new BigDecimal("500"));
        verify(financialGoalRepository).saveAll(goals);
    }
}

