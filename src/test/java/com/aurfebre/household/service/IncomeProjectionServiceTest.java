package com.aurfebre.household.service;

import com.aurfebre.household.dto.IncomeProjectionRequest;
import com.aurfebre.household.dto.IncomeProjectionResponse;
import com.aurfebre.household.domain.IncomeProjection;
import com.aurfebre.household.repository.IncomeProjectionRepository;
import com.aurfebre.household.security.CustomUserPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncomeProjectionServiceTest {

    @Mock
    private IncomeProjectionRepository incomeProjectionRepository;

    @InjectMocks
    private IncomeProjectionService incomeProjectionService;

    @BeforeEach
    void setUp() {
        CustomUserPrincipal principal = new CustomUserPrincipal(
                1L,
                "test@example.com",
                "",
                "Test User",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getTotalProjectedIncomeForYear_WhenDataExists_ShouldReturnTotal() {
        BigDecimal total = new BigDecimal("5000000");
        when(incomeProjectionRepository.sumProjectedIncomeByUserIdAndYear(1L, 2025))
                .thenReturn(Optional.of(total));

        BigDecimal result = incomeProjectionService.getTotalProjectedIncomeForYear(2025);

        assertThat(result).isEqualByComparingTo(total);
        verify(incomeProjectionRepository).sumProjectedIncomeByUserIdAndYear(1L, 2025);
    }

    @Test
    void getTotalProjectedIncomeForYear_WhenNoData_ShouldReturnZero() {
        when(incomeProjectionRepository.sumProjectedIncomeByUserIdAndYear(1L, 2025))
                .thenReturn(Optional.empty());

        BigDecimal result = incomeProjectionService.getTotalProjectedIncomeForYear(2025);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        verify(incomeProjectionRepository).sumProjectedIncomeByUserIdAndYear(1L, 2025);
    }
}
