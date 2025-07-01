package com.aurfebre.household.service;

import com.aurfebre.household.domain.Category;
import com.aurfebre.household.domain.enums.CategoryType;
import com.aurfebre.household.domain.enums.SubCategoryType;
import com.aurfebre.household.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Category> getCategoriesByUserId(Long userId) {
        return categoryRepository.findByUserIdAndIsActiveTrue(userId);
    }

    @Transactional(readOnly = true)
    public List<Category> getCategoriesByUserIdAndType(Long userId, CategoryType type) {
        return categoryRepository.findByUserIdAndTypeAndIsActiveTrue(userId, type);
    }

    @Transactional(readOnly = true)
    public List<Category> getCategoriesByUserIdAndSubType(Long userId, SubCategoryType subType) {
        return categoryRepository.findByUserIdAndSubTypeAndIsActiveTrue(userId, subType);
    }

    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category createCategory(Category category) {
        if (categoryRepository.existsByUserIdAndNameAndIsActiveTrue(category.getUserId(), category.getName())) {
            throw new IllegalArgumentException("Category with name '" + category.getName() + "' already exists for this user");
        }
        
        if (category.getSubType().getParentType() != category.getType()) {
            throw new IllegalArgumentException("SubType " + category.getSubType() + " is not compatible with Type " + category.getType());
        }
        
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

        if (!category.getName().equals(categoryDetails.getName()) &&
            categoryRepository.existsByUserIdAndNameAndIsActiveTrue(category.getUserId(), categoryDetails.getName())) {
            throw new IllegalArgumentException("Category with name '" + categoryDetails.getName() + "' already exists for this user");
        }

        if (categoryDetails.getSubType().getParentType() != categoryDetails.getType()) {
            throw new IllegalArgumentException("SubType " + categoryDetails.getSubType() + " is not compatible with Type " + categoryDetails.getType());
        }

        category.setName(categoryDetails.getName());
        category.setType(categoryDetails.getType());
        category.setSubType(categoryDetails.getSubType());
        category.setIcon(categoryDetails.getIcon());
        category.setColor(categoryDetails.getColor());
        
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));
        
        category.setIsActive(false);
        categoryRepository.save(category);
    }

    public void hardDeleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}