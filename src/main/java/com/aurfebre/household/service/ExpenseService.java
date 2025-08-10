package com.aurfebre.household.service;

import com.aurfebre.household.domain.Expense;
import com.aurfebre.household.dto.ExpenseRequest;
import com.aurfebre.household.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Transactional(readOnly = true)
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public Expense createExpense(ExpenseRequest expenseRequest) {
        Expense expense = new Expense(
                expenseRequest.getDescription(),
                expenseRequest.getAmount(),
                expenseRequest.getCategory()
        );
        return expenseRepository.save(expense);
    }
}