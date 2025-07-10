package com.aurfebre.household.api;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/login-page")
    public Map<String, String> loginPage() {
        return Map.of(
            "googleLoginUrl", "/oauth2/authorization/google",
            "message", "Click the link to login with Google"
        );
    }
}