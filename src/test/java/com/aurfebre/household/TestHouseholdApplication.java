package com.aurfebre.household;

import org.springframework.boot.SpringApplication;

public class TestHouseholdApplication {

	public static void main(String[] args) {
		SpringApplication.from(HouseholdApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
