package com.aurfebre.household.service;

import com.aurfebre.household.domain.FinancialGoal;
import com.aurfebre.household.domain.enums.GoalType;
import com.aurfebre.household.repository.FinancialGoalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinancialGoalServiceTest {

    @Mock
    private FinancialGoalRepository financialGoalRepository;

    @InjectMocks
    private FinancialGoalService financialGoalService;

    @Test
    void getGoalsForWeek_ShouldFilterByWeekRange() {
        FinancialGoal goal1 = new FinancialGoal(1L, GoalType.SAVING, "Goal1", new BigDecimal("1000"),
                LocalDate.of(2024,1,4), 1);
        FinancialGoal goal2 = new FinancialGoal(1L, GoalType.SAVING, "Goal2", new BigDecimal("2000"),
                LocalDate.of(2024,1,10), 1);
        when(financialGoalRepository.findGoalsDueByDate(1L, LocalDate.of(2024,1,7)))
                .thenReturn(List.of(goal1));

        List<FinancialGoal> result = financialGoalService.getGoalsForWeek(1L, DayOfWeek.MONDAY, LocalDate.of(2024,1,3));

        assertThat(result).containsExactly(goal1);
    }
}
