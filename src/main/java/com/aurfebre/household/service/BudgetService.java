package com.aurfebre.household.service;

import com.aurfebre.household.domain.Budget;
import com.aurfebre.household.domain.enums.BudgetPeriod;
import com.aurfebre.household.repository.BudgetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BudgetService {

    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @Transactional(readOnly = true)
    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Budget> getBudgetsByUserId(Long userId) {
        return budgetRepository.findByUserIdAndIsActiveTrueOrderByStartDateDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Budget> getBudgetsByUserIdAndPeriod(Long userId, BudgetPeriod period) {
        return budgetRepository.findByUserIdAndPeriodAndIsActiveTrueOrderByStartDateDesc(userId, period);
    }

    @Transactional(readOnly = true)
    public Optional<Budget> getBudgetByUserIdAndCategory(Long userId, Long categoryId) {
        return budgetRepository.findByUserIdAndCategoryIdAndIsActiveTrue(userId, categoryId);
    }

    @Transactional(readOnly = true)
    public List<Budget> getActiveBudgetsForDate(Long userId, LocalDate date) {
        return budgetRepository.findActiveBudgetsForDate(userId, date);
    }

    @Transactional(readOnly = true)
    public Optional<Budget> getActiveBudgetForCategoryAndDate(Long userId, Long categoryId, LocalDate date) {
        return budgetRepository.findActiveBudgetForCategoryAndDate(userId, categoryId, date);
    }

    @Transactional(readOnly = true)
    public Optional<Budget> getBudgetById(Long id) {
        return budgetRepository.findById(id);
    }

    public Budget createBudget(Budget budget) {
        if (budget.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Budget amount must be greater than zero");
        }
        
        if (budget.getEndDate().isBefore(budget.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        
        if (budget.getCategoryId() != null && 
            budgetRepository.existsByUserIdAndCategoryIdAndPeriodAndIsActiveTrue(
                budget.getUserId(), budget.getCategoryId(), budget.getPeriod())) {
            throw new IllegalArgumentException("Active budget already exists for this category and period");
        }
        
        return budgetRepository.save(budget);
    }

    public Budget updateBudget(Long id, Budget budgetDetails) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found with id: " + id));

        if (budgetDetails.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Budget amount must be greater than zero");
        }
        
        if (budgetDetails.getEndDate().isBefore(budgetDetails.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        budget.setCategoryId(budgetDetails.getCategoryId());
        budget.setPeriod(budgetDetails.getPeriod());
        budget.setAmount(budgetDetails.getAmount());
        budget.setStartDate(budgetDetails.getStartDate());
        budget.setEndDate(budgetDetails.getEndDate());
        
        return budgetRepository.save(budget);
    }

    public void deleteBudget(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found with id: " + id));
        
        budget.setIsActive(false);
        budgetRepository.save(budget);
    }

    public void hardDeleteBudget(Long id) {
        if (!budgetRepository.existsById(id)) {
            throw new IllegalArgumentException("Budget not found with id: " + id);
        }
        budgetRepository.deleteById(id);
    }

    public void activateBudget(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found with id: " + id));
        
        budget.setIsActive(true);
        budgetRepository.save(budget);
    }
}