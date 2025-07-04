package com.aurfebre.household.dto;

import java.math.BigDecimal;

public record MonthlyExpenseComparison(
    BigDecimal currentMonth,
    BigDecimal previousMonth,
    BigDecimal difference,
    Double percentageChange
) {}