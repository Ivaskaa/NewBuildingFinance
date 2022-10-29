package com.example.NewBuildingFinance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NewBuildingFinanceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewBuildingFinanceApplication.class, args);
	}

}
