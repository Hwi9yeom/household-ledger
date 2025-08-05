package com.aurfebre.household.service;

import com.aurfebre.household.domain.FinancialGoal;
import com.aurfebre.household.repository.FinancialGoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class FinancialGoalService {

    private final FinancialGoalRepository repository;

    public FinancialGoalService(FinancialGoalRepository repository) {
        this.repository = repository;
    }

    public List<FinancialGoal> getGoalsForWeek(Long userId, DayOfWeek weekStartDay, LocalDate currentWeek) {
        LocalDate start = currentWeek.with(TemporalAdjusters.previousOrSame(weekStartDay));
        LocalDate end = start.plusDays(6);
        return repository.findGoalsDueByDate(userId, end).stream()
                .filter(goal -> !goal.getTargetDate().isBefore(start))
                .collect(Collectors.toList());
    }
}
