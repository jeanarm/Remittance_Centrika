package com.centrika.remittance;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RemittanceApplication {

	public static void main(String[] args) {
		loadEnvironmentVariables();
		SpringApplication.run(RemittanceApplication.class, args);
	}

	private static void loadEnvironmentVariables() {
		String deploymentEnv = System.getenv("DEPLOYMENT_ENV");

		if (deploymentEnv == null || deploymentEnv.equalsIgnoreCase("local")) {
			// Load from .env in local development
			Dotenv dotenv = Dotenv.configure()
					.directory("./")
					.ignoreIfMissing()
					.load();
			dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

			System.out.println("Loaded environment variables from .env (Local Environment)");
		} else {
			// Use system environment variables on test server
			System.out.println("🚀 Running in " + deploymentEnv + " mode. Using system environment variables.");
		}
	}
}