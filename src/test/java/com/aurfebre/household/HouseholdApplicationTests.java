package com.aurfebre.household;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import static io.restassured.RestAssured.given;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
