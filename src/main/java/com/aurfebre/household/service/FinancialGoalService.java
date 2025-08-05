package com.aurfebre.household.service;

import com.aurfebre.household.domain.FinancialGoal;
import com.aurfebre.household.repository.FinancialGoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FinancialGoalService {

    private final FinancialGoalRepository financialGoalRepository;
    private final UserBudgetSettingsService userBudgetSettingsService;

    public FinancialGoalService(FinancialGoalRepository financialGoalRepository,
                                UserBudgetSettingsService userBudgetSettingsService) {
        this.financialGoalRepository = financialGoalRepository;
        this.userBudgetSettingsService = userBudgetSettingsService;
    }

    @Transactional(readOnly = true)
    public List<FinancialGoal> getGoalsByUserId(Long userId) {
        return financialGoalRepository.findByUserIdAndIsActiveTrueOrderByPriorityAsc(userId);
    }

    @Transactional(readOnly = true)
    public List<FinancialGoal> getGoalsDueByMonth(Long userId, YearMonth yearMonth) {
        int monthStartDay = userBudgetSettingsService.getSettings(userId).getMonthStartDay();
        LocalDate start = yearMonth.atDay(monthStartDay);
        LocalDate end = start.plusMonths(1).minusDays(1);
        return financialGoalRepository.findGoalsDueByDate(userId, end);
    }

    @Transactional(readOnly = true)
    public Optional<FinancialGoal> getGoalById(Long id) {
        return financialGoalRepository.findById(id);
    }

    public FinancialGoal createGoal(FinancialGoal goal) {
        return financialGoalRepository.save(goal);
    }

    public FinancialGoal updateGoal(Long id, FinancialGoal goalDetails) {
        FinancialGoal goal = financialGoalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FinancialGoal not found with id: " + id));

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
        financialGoalRepository.deleteById(id);
    }
}
