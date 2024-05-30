package com.ms.keycloak.ms.keycloak;

import com.ms.keycloak.service.AdminClientService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		AdminClientService adminClientService = new AdminClientService();
		adminClientService.searchUsersByDeletionDate();
	}

}
