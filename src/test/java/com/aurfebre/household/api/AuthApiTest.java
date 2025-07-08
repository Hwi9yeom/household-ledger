package com.aurfebre.household.api;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.aurfebre.household.repository.UserRepository;
import com.aurfebre.household.domain.User;
import org.springframework.transaction.annotation.Transactional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        userRepository.deleteAll();
        
        User testUser = new User(
            "testuser@example.com",
            "testuser",
            passwordEncoder.encode("testpass123"),
            "Test User"
        );
        userRepository.save(testUser);
    }

    @Test
    void should_issue_jwt_on_valid_login() {
        given()
            .contentType("application/json")
            .body("""
                {
                    "username": "testuser",
                    "password": "testpass123"
                }
                """)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(HttpStatus.OK.value())
            .header("Authorization", notNullValue())
            .header("Authorization", startsWith("Bearer "))
            .body("token", notNullValue())
            .body("username", equalTo("testuser"));
    }

    @Test
    void should_reject_invalid_credentials() {
        given()
            .contentType("application/json")
            .body("""
                {
                    "username": "testuser",
                    "password": "wrongpassword"
                }
                """)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }
}