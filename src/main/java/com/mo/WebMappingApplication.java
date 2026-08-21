package com.mo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@AutoConfiguration

public class WebMappingApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebMappingApplication.class, args);
	}

}

