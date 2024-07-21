package com.example.paymentoptimization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class PaymentOptimizationApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentOptimizationApplication.class, args);
	}

}
