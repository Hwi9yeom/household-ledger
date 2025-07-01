package com.aurfebre.household.api;

import com.aurfebre.household.domain.Category;
import com.aurfebre.household.domain.enums.CategoryType;
import com.aurfebre.household.domain.enums.SubCategoryType;
import com.aurfebre.household.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/user/{userId}")
    public List<Category> getCategoriesByUserId(@PathVariable Long userId) {
        return categoryService.getCategoriesByUserId(userId);
    }

    @GetMapping("/user/{userId}/type/{type}")
    public List<Category> getCategoriesByUserIdAndType(
            @PathVariable Long userId, 
            @PathVariable CategoryType type) {
        return categoryService.getCategoriesByUserIdAndType(userId, type);
    }

    @GetMapping("/user/{userId}/subtype/{subType}")
    public List<Category> getCategoriesByUserIdAndSubType(
            @PathVariable Long userId, 
            @PathVariable SubCategoryType subType) {
        return categoryService.getCategoriesByUserIdAndSubType(userId, subType);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(category -> ResponseEntity.ok().body(category))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Category createCategory(@RequestBody Category category) {
        return categoryService.createCategory(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails) {
        try {
            Category updatedCategory = categoryService.updateCategory(id, categoryDetails);
            return ResponseEntity.ok(updatedCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}/hard")
    public ResponseEntity<?> hardDeleteCategory(@PathVariable Long id) {
        try {
            categoryService.hardDeleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}