package com.aurfebre.household.service;

import com.aurfebre.household.domain.Category;
import com.aurfebre.household.domain.enums.CategoryType;
import com.aurfebre.household.domain.enums.SubCategoryType;
import com.aurfebre.household.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category(1L, "월급", CategoryType.INCOME, SubCategoryType.FIXED_INCOME);
        testCategory.setId(1L);
        testCategory.setIcon("💰");
        testCategory.setColor("#4CAF50");
        testCategory.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getAllCategories_ShouldReturnAllCategories() {
        // Given
        List<Category> categories = Arrays.asList(
            testCategory,
            new Category(1L, "식비", CategoryType.EXPENSE, SubCategoryType.VARIABLE_EXPENSE)
        );
        when(categoryRepository.findAll()).thenReturn(categories);

        // When
        List<Category> result = categoryService.getAllCategories();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).contains(testCategory);
        verify(categoryRepository).findAll();
    }

    @Test
    void getCategoriesByUserId_ShouldReturnActiveCategories() {
        // Given
        List<Category> categories = Arrays.asList(testCategory);
        when(categoryRepository.findByUserIdAndIsActiveTrue(1L)).thenReturn(categories);

        // When
        List<Category> result = categoryService.getCategoriesByUserId(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).contains(testCategory);
        verify(categoryRepository).findByUserIdAndIsActiveTrue(1L);
    }

    @Test
    void getCategoriesByUserIdAndType_ShouldReturnFilteredCategories() {
        // Given
        List<Category> categories = Arrays.asList(testCategory);
        when(categoryRepository.findByUserIdAndTypeAndIsActiveTrue(1L, CategoryType.INCOME))
            .thenReturn(categories);

        // When
        List<Category> result = categoryService.getCategoriesByUserIdAndType(1L, CategoryType.INCOME);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo(CategoryType.INCOME);
        verify(categoryRepository).findByUserIdAndTypeAndIsActiveTrue(1L, CategoryType.INCOME);
    }

    @Test
    void getCategoriesByUserIdAndSubType_ShouldReturnFilteredCategories() {
        // Given
        List<Category> categories = Arrays.asList(testCategory);
        when(categoryRepository.findByUserIdAndSubTypeAndIsActiveTrue(1L, SubCategoryType.FIXED_INCOME))
            .thenReturn(categories);

        // When
        List<Category> result = categoryService.getCategoriesByUserIdAndSubType(1L, SubCategoryType.FIXED_INCOME);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSubType()).isEqualTo(SubCategoryType.FIXED_INCOME);
        verify(categoryRepository).findByUserIdAndSubTypeAndIsActiveTrue(1L, SubCategoryType.FIXED_INCOME);
    }

    @Test
    void getCategoryById_WhenCategoryExists_ShouldReturnCategory() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // When
        Optional<Category> result = categoryService.getCategoryById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testCategory);
        verify(categoryRepository).findById(1L);
    }

    @Test
    void createCategory_WhenValidCategory_ShouldCreateCategory() {
        // Given
        Category newCategory = new Category(1L, "새 카테고리", CategoryType.EXPENSE, SubCategoryType.VARIABLE_EXPENSE);
        when(categoryRepository.existsByUserIdAndNameAndIsActiveTrue(1L, "새 카테고리")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);

        // When
        Category result = categoryService.createCategory(newCategory);

        // Then
        assertThat(result).isEqualTo(newCategory);
        verify(categoryRepository).existsByUserIdAndNameAndIsActiveTrue(1L, "새 카테고리");
        verify(categoryRepository).save(newCategory);
    }

    @Test
    void createCategory_WhenNameAlreadyExists_ShouldThrowException() {
        // Given
        Category newCategory = new Category(1L, "월급", CategoryType.INCOME, SubCategoryType.FIXED_INCOME);
        when(categoryRepository.existsByUserIdAndNameAndIsActiveTrue(1L, "월급")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> categoryService.createCategory(newCategory))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Category with name '월급' already exists for this user");
        
        verify(categoryRepository).existsByUserIdAndNameAndIsActiveTrue(1L, "월급");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void createCategory_WhenIncompatibleSubType_ShouldThrowException() {
        // Given
        Category invalidCategory = new Category(1L, "잘못된 카테고리", CategoryType.INCOME, SubCategoryType.VARIABLE_EXPENSE);

        // When & Then
        assertThatThrownBy(() -> categoryService.createCategory(invalidCategory))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("SubType VARIABLE_EXPENSE is not compatible with Type INCOME");
        
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_WhenValidUpdate_ShouldUpdateCategory() {
        // Given
        Category updateDetails = new Category(1L, "수정된 월급", CategoryType.INCOME, SubCategoryType.FIXED_INCOME);
        updateDetails.setIcon("💵");
        updateDetails.setColor("#2E7D32");
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.existsByUserIdAndNameAndIsActiveTrue(1L, "수정된 월급")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // When
        Category result = categoryService.updateCategory(1L, updateDetails);

        // Then
        assertThat(result.getName()).isEqualTo("수정된 월급");
        assertThat(result.getIcon()).isEqualTo("💵");
        assertThat(result.getColor()).isEqualTo("#2E7D32");
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(testCategory);
    }

    @Test
    void updateCategory_WhenCategoryNotExists_ShouldThrowException() {
        // Given
        Category updateDetails = new Category(1L, "수정된 월급", CategoryType.INCOME, SubCategoryType.FIXED_INCOME);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.updateCategory(1L, updateDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Category not found with id: 1");
        
        verify(categoryRepository).findById(1L);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_WhenCategoryExists_ShouldDeactivateCategory() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // When
        categoryService.deleteCategory(1L);

        // Then
        assertThat(testCategory.getIsActive()).isFalse();
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(testCategory);
    }

    @Test
    void deleteCategory_WhenCategoryNotExists_ShouldThrowException() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.deleteCategory(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Category not found with id: 1");
        
        verify(categoryRepository).findById(1L);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void hardDeleteCategory_WhenCategoryExists_ShouldDeletePermanently() {
        // Given
        when(categoryRepository.existsById(1L)).thenReturn(true);

        // When
        categoryService.hardDeleteCategory(1L);

        // Then
        verify(categoryRepository).existsById(1L);
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void hardDeleteCategory_WhenCategoryNotExists_ShouldThrowException() {
        // Given
        when(categoryRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> categoryService.hardDeleteCategory(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Category not found with id: 1");
        
        verify(categoryRepository).existsById(1L);
        verify(categoryRepository, never()).deleteById(any());
    }
}