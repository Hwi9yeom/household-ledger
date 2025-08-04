package com.aurfebre.household.service;

import com.aurfebre.household.domain.FinancialGoal;
import com.aurfebre.household.repository.FinancialGoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FinancialGoalService {

    private final FinancialGoalRepository financialGoalRepository;

    public FinancialGoalService(FinancialGoalRepository financialGoalRepository) {
        this.financialGoalRepository = financialGoalRepository;
    }

    @Transactional(readOnly = true)
    public List<FinancialGoal> getGoalsByUserId(Long userId) {
        return financialGoalRepository.findByUserIdAndIsActiveTrueOrderByPriorityAsc(userId);
    }

    @Transactional(readOnly = true)
    public Optional<FinancialGoal> getGoalById(Long id) {
        return financialGoalRepository.findById(id);
    }

    public FinancialGoal createGoal(FinancialGoal goal) {
        validateGoal(goal);
        if (goal.getPriority() != null &&
            financialGoalRepository.existsByUserIdAndPriorityAndIsActiveTrue(goal.getUserId(), goal.getPriority())) {
            throw new IllegalArgumentException("Active goal already exists with this priority");
        }
        return financialGoalRepository.save(goal);
    }

    public FinancialGoal updateGoal(Long id, FinancialGoal goalDetails) {
        FinancialGoal goal = financialGoalRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Goal not found with id: " + id));

        validateGoal(goalDetails);

        if (goalDetails.getPriority() != null && !goalDetails.getPriority().equals(goal.getPriority()) &&
            financialGoalRepository.existsByUserIdAndPriorityAndIsActiveTrue(goal.getUserId(), goalDetails.getPriority())) {
            throw new IllegalArgumentException("Active goal already exists with this priority");
        }

        goal.setGoalType(goalDetails.getGoalType());
        goal.setName(goalDetails.getName());
        goal.setTargetAmount(goalDetails.getTargetAmount());
        goal.setCurrentAmount(goalDetails.getCurrentAmount());
        goal.setTargetDate(goalDetails.getTargetDate());
        goal.setMonthlyContribution(goalDetails.getMonthlyContribution());
        goal.setPriority(goalDetails.getPriority());

        return financialGoalRepository.save(goal);
    }

    public void deleteGoal(Long id) {
        FinancialGoal goal = financialGoalRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Goal not found with id: " + id));
        goal.setIsActive(false);
        financialGoalRepository.save(goal);
    }

    public void hardDeleteGoal(Long id) {
        if (!financialGoalRepository.existsById(id)) {
            throw new IllegalArgumentException("Goal not found with id: " + id);
        }
        financialGoalRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public BigDecimal getProgress(Long goalId) {
        FinancialGoal goal = financialGoalRepository.findById(goalId)
            .orElseThrow(() -> new IllegalArgumentException("Goal not found with id: " + goalId));
        return calculateProgress(goal);
    }

    private BigDecimal calculateProgress(FinancialGoal goal) {
        if (goal.getTargetAmount() == null || goal.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal progress = goal.getCurrentAmount().divide(goal.getTargetAmount(), 4, RoundingMode.HALF_UP);
        return progress.multiply(BigDecimal.valueOf(100));
    }

    public void applyMonthlyContribution() {
        List<FinancialGoal> goals = financialGoalRepository.findAll();
        for (FinancialGoal goal : goals) {
            if (Boolean.TRUE.equals(goal.getIsActive()) && goal.getMonthlyContribution() != null) {
                goal.setCurrentAmount(goal.getCurrentAmount().add(goal.getMonthlyContribution()));
            }
        }
        financialGoalRepository.saveAll(goals);
    }

    private void validateGoal(FinancialGoal goal) {
        if (goal.getTargetAmount() == null || goal.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Target amount must be greater than zero");
        }
    }
}

