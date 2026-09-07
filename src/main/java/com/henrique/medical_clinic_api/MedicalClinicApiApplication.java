package com.henrique.medical_clinic_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MedicalClinicApiApplication {
	static void main(String[] args) {
		SpringApplication.run(MedicalClinicApiApplication.class, args);
	}
}
