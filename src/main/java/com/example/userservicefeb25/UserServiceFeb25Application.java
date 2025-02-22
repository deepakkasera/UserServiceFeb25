package com.example.userservicefeb25;

import jakarta.persistence.EntityListeners;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class UserServiceFeb25Application {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceFeb25Application.class, args);
    }

}
