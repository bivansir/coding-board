package ru.coding_board.session_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
    info = @Info(
        title = "Session Service API",
        version = "1.0",
        description = "API для управления процессом лайв-кодинга: кандидаты, сессии, задачи, фидбек"
    )
)
@SpringBootApplication
public class SessionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SessionServiceApplication.class, args);
	}

}
