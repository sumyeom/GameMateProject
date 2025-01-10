package com.example.gamemate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GameMateApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameMateApplication.class, args);
    }

}
