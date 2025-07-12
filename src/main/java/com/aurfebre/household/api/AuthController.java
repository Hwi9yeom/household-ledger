package com.aurfebre.household.api;

import com.aurfebre.household.dto.auth.LoginRequest;
import com.aurfebre.household.dto.auth.LoginResponse;
import com.aurfebre.household.security.CustomUserPrincipal;
import com.aurfebre.household.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        CustomUserPrincipal userPrincipal = (CustomUserPrincipal) authentication.getPrincipal();

        LoginResponse response = LoginResponse.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .userId(userPrincipal.getId())
                .email(userPrincipal.getEmail())
                .name(userPrincipal.getName())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/login-page")
    public Map<String, String> loginPage() {
        return Map.of(
            "googleLoginUrl", "/oauth2/authorization/google",
            "message", "Click the link to login with Google"
        );
    }
}