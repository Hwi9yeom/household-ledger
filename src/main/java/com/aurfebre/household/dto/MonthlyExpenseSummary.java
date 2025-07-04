package com.aurfebre.household.dto;

import java.math.BigDecimal;
import java.util.Map;

public record MonthlyExpenseSummary(
    Map<String, BigDecimal> categoryExpenses,
    BigDecimal total
) {}