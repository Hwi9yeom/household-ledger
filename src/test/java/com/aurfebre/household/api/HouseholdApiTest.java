package com.aurfebre.household.api;

import com.aurfebre.household.TestcontainersConfiguration;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class HouseholdApiTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void should_return_empty_list_when_no_expenses_exist() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/expenses")
        .then()
            .statusCode(200)
            .body("size()", equalTo(0));
    }

    @Test
    void should_create_new_expense() {
        String expenseJson = """
            {
                "description": "테스트 지출",
                "amount": 10000,
                "category": "FOOD"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(expenseJson)
        .when()
            .post("/api/expenses")
        .then()
            .statusCode(201)
            .body("description", equalTo("테스트 지출"))
            .body("amount", equalTo(10000))
            .body("category", equalTo("FOOD"));
    }
}