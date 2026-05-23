package com.tracker.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

@SpringBootApplication
public class WebTrackerApplication {
    
    public static void main(String[] args) {
		SpringApplication.run(WebTrackerApplication.class, args);
	}

}
