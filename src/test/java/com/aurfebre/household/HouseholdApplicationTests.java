package com.aurfebre.household;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class HouseholdApplicationTests {

	@LocalServerPort
	int port;


	@Test
	void contextLoads() {
	}

	@Test
	void healthEndpointReturns200() {
		given().port(port).when().get("/health").then().statusCode(200);
	}

}
