package com.aurfebre.household.mapper;

import com.aurfebre.household.domain.UserBudgetSettings;
import com.aurfebre.household.dto.UserBudgetSettingsDto;

public class UserBudgetSettingsMapper {
    public static UserBudgetSettingsDto toDto(UserBudgetSettings settings) {
        if (settings == null) {
            return null;
        }
        return new UserBudgetSettingsDto(
                settings.getUserId(),
                settings.getMonthStartDay(),
                settings.getMonthlySalary()
        );
    }

    public static UserBudgetSettings toEntity(UserBudgetSettingsDto dto) {
        if (dto == null) {
            return null;
        }
        return new UserBudgetSettings(
                dto.getUserId(),
                dto.getMonthStartDay(),
                dto.getMonthlySalary()
        );
    }
}
