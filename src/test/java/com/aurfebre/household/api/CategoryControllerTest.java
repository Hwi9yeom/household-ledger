package com.aurfebre.household.api;

import com.aurfebre.household.domain.Category;
import com.aurfebre.household.domain.enums.CategoryType;
import com.aurfebre.household.domain.enums.SubCategoryType;
import com.aurfebre.household.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@Import(com.aurfebre.household.config.SecurityConfig.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category(1L, "Food", CategoryType.EXPENSE, SubCategoryType.VARIABLE_EXPENSE);
        testCategory.setId(1L);
        testCategory.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getAllCategories_ShouldReturnAllCategories() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(testCategory));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Food")));
    }

    @Test
    void getCategoryById_WhenExists_ShouldReturnCategory() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(testCategory));

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void getCategoryById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createCategory_WhenValid_ShouldReturnCreated() throws Exception {
        when(categoryService.createCategory(any(Category.class))).thenReturn(testCategory);

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCategory)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void updateCategory_WhenNotFound_ShouldReturnNotFound() throws Exception {
        when(categoryService.updateCategory(eq(99L), any(Category.class)))
                .thenThrow(new IllegalArgumentException("Category not found"));

        mockMvc.perform(put("/api/categories/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCategory)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCategoriesByUserIdAndType_WhenInvalidType_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/categories/user/1/type/INVALID"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCategory_WhenNotFound_ShouldReturnNotFound() throws Exception {
        doThrow(new IllegalArgumentException("Category not found")).when(categoryService).deleteCategory(99L);

        mockMvc.perform(delete("/api/categories/99"))
                .andExpect(status().isNotFound());
    }
}

