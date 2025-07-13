package com.aurfebre.household.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityUtilsTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserId_WhenUserIsAuthenticated_ShouldReturnUserId() {
        // Given
        CustomUserPrincipal userPrincipal = new CustomUserPrincipal(
                1L,
                "test@example.com",
                "",
                "Test User",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        
        UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // When
        Long userId = SecurityUtils.getCurrentUserId();

        // Then
        assertThat(userId).isEqualTo(1L);
    }

    @Test
    void getCurrentUserId_WhenUserIsNotAuthenticated_ShouldReturnNull() {
        // When (no authentication set)
        Long userId = SecurityUtils.getCurrentUserId();

        // Then
        assertThat(userId).isNull();
    }

    @Test
    void getCurrentUserId_WhenAnonymousUser_ShouldReturnNull() {
        // Given
        UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken("anonymousUser", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // When
        Long userId = SecurityUtils.getCurrentUserId();

        // Then
        assertThat(userId).isNull();
    }

    @Test
    void getCurrentUserEmail_WhenUserIsAuthenticated_ShouldReturnEmail() {
        // Given
        CustomUserPrincipal userPrincipal = new CustomUserPrincipal(
                1L,
                "test@example.com",
                "",
                "Test User",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        
        UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // When
        String email = SecurityUtils.getCurrentUserEmail();

        // Then
        assertThat(email).isEqualTo("test@example.com");
    }

    @Test
    void getCurrentUserEmail_WhenUserIsNotAuthenticated_ShouldReturnNull() {
        // When (no authentication set)
        String email = SecurityUtils.getCurrentUserEmail();

        // Then
        assertThat(email).isNull();
    }

    @Test
    void isCurrentUserAuthenticated_WhenUserIsAuthenticated_ShouldReturnTrue() {
        // Given
        CustomUserPrincipal userPrincipal = new CustomUserPrincipal(
                1L,
                "test@example.com",
                "",
                "Test User",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        
        UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // When
        boolean isAuthenticated = SecurityUtils.isCurrentUserAuthenticated();

        // Then
        assertThat(isAuthenticated).isTrue();
    }

    @Test
    void isCurrentUserAuthenticated_WhenUserIsNotAuthenticated_ShouldReturnFalse() {
        // When (no authentication set)
        boolean isAuthenticated = SecurityUtils.isCurrentUserAuthenticated();

        // Then
        assertThat(isAuthenticated).isFalse();
    }

    @Test
    void isCurrentUserAuthenticated_WhenAnonymousUser_ShouldReturnFalse() {
        // Given
        UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken("anonymousUser", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // When
        boolean isAuthenticated = SecurityUtils.isCurrentUserAuthenticated();

        // Then
        assertThat(isAuthenticated).isFalse();
    }
}