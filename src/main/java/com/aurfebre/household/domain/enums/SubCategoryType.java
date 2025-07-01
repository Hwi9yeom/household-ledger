package com.aurfebre.household.domain.enums;

public enum SubCategoryType {
    // 수입 카테고리
    FIXED_INCOME("고정수입", CategoryType.INCOME),
    VARIABLE_INCOME("변동수입", CategoryType.INCOME),
    
    // 지출 카테고리
    FIXED_EXPENSE("고정지출", CategoryType.EXPENSE),
    VARIABLE_EXPENSE("변동지출", CategoryType.EXPENSE),
    
    // 저축/투자 카테고리
    SAVING("저축", CategoryType.SAVING_INVESTMENT),
    INVESTMENT("투자", CategoryType.SAVING_INVESTMENT);

    private final String description;
    private final CategoryType parentType;

    SubCategoryType(String description, CategoryType parentType) {
        this.description = description;
        this.parentType = parentType;
    }

    public String getDescription() {
        return description;
    }

    public CategoryType getParentType() {
        return parentType;
    }
}