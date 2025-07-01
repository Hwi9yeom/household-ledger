package com.aurfebre.household.repository;

import com.aurfebre.household.domain.Category;
import com.aurfebre.household.domain.enums.CategoryType;
import com.aurfebre.household.domain.enums.SubCategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    List<Category> findByUserIdAndIsActiveTrue(Long userId);
    
    List<Category> findByUserIdAndTypeAndIsActiveTrue(Long userId, CategoryType type);
    
    List<Category> findByUserIdAndSubTypeAndIsActiveTrue(Long userId, SubCategoryType subType);
    
    boolean existsByUserIdAndNameAndIsActiveTrue(Long userId, String name);
}