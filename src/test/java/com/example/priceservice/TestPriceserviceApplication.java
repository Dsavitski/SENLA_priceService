package com.example.priceservice;

import org.springframework.boot.SpringApplication;

public class TestPriceserviceApplication {

	public static void main(String[] args) {
		SpringApplication.from(PriceserviceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
