package com.myproject.clinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Lớp class ClinicApplication trong hệ thống.
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class ClinicApplication {

	/**
	 * Phương thức: Main.
	 */
	public static void main(String[] args) {
		SpringApplication.run(ClinicApplication.class, args);
	}

}
