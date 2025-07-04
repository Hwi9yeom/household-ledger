package com.aurfebre.household.service;

import com.aurfebre.household.domain.LedgerEntry;
import com.aurfebre.household.domain.enums.EntryType;
import com.aurfebre.household.dto.MonthlyExpenseComparison;
import com.aurfebre.household.dto.MonthlyExpenseSummary;
import com.aurfebre.household.repository.LedgerEntryRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MonthlyExpenseService {

    private final LedgerEntryRepository ledgerEntryRepository;

    public MonthlyExpenseService(LedgerEntryRepository ledgerEntryRepository) {
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    public List<LedgerEntry> getMonthlyExpenses(Long userId, YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        return ledgerEntryRepository.findByUserIdAndEntryTypeAndDateBetweenOrderByDateDesc(
            userId, EntryType.EXPENSE, startDate, endDate);
    }

    public MonthlyExpenseSummary getMonthlyExpensesSummary(Long userId, YearMonth yearMonth) {
        List<LedgerEntry> expenses = getMonthlyExpenses(userId, yearMonth);
        Map<String, BigDecimal> categoryExpenses = new HashMap<>();
        
        // 카테고리별 합계 계산 (실제로는 Category 엔티티와 조인해야 하지만 일단 단순화)
        BigDecimal total = BigDecimal.ZERO;
        for (LedgerEntry expense : expenses) {
            total = total.add(expense.getAmount());
            
            // 임시로 description을 카테고리명으로 사용
            String categoryName = expense.getDescription();
            categoryExpenses.put(categoryName, categoryExpenses.getOrDefault(categoryName, BigDecimal.ZERO).add(expense.getAmount()));
        }
        
        return new MonthlyExpenseSummary(categoryExpenses, total);
    }

    public MonthlyExpenseComparison getMonthlyComparison(Long userId, YearMonth currentMonth) {
        YearMonth previousMonth = currentMonth.minusMonths(1);
        
        BigDecimal currentTotal = getTotalExpenseForMonth(userId, currentMonth);
        BigDecimal previousTotal = getTotalExpenseForMonth(userId, previousMonth);
        
        BigDecimal difference = currentTotal.subtract(previousTotal);
        double percentageChange = 0.0;
        
        if (previousTotal.compareTo(BigDecimal.ZERO) > 0) {
            percentageChange = difference.divide(previousTotal, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .doubleValue();
        }
        
        return new MonthlyExpenseComparison(
            currentTotal,
            previousTotal,
            difference,
            Math.round(percentageChange * 100.0) / 100.0
        );
    }

    private BigDecimal getTotalExpenseForMonth(Long userId, YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        BigDecimal total = ledgerEntryRepository.sumByUserIdAndEntryTypeAndDateBetween(
            userId, EntryType.EXPENSE, startDate, endDate);
        
        return total != null ? total : BigDecimal.ZERO;
    }
}