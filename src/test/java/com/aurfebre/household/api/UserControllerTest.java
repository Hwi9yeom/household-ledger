package com.aurfebre.household.api;

import com.aurfebre.household.domain.User;
import com.aurfebre.household.service.UserService;
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

@WebMvcTest(UserController.class)
@Import(com.aurfebre.household.config.SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "Test User");
        testUser.setId(1L);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(testUser));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].email", is("test@example.com")));
    }

    @Test
    void getUserById_WhenExists_ShouldReturnUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void getUserById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_WhenValid_ShouldReturnCreated() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void updateUser_WhenNotFound_ShouldReturnNotFound() throws Exception {
        when(userService.updateUser(eq(99L), any(User.class)))
                .thenThrow(new IllegalArgumentException("User not found with id: 99"));

        mockMvc.perform(put("/api/users/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserById_WhenInvalidId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/users/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUser_WhenNotFound_ShouldReturnNotFound() throws Exception {
        doThrow(new IllegalArgumentException("User not found")).when(userService).deleteUser(99L);

        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound());
    }
}

