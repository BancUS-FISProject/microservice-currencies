package com.bankUS.microservice_currencies;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MicroserviceCurrenciesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceCurrenciesApplication.class, args);
	}

}
