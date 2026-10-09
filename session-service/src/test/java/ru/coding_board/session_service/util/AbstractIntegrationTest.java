package ru.coding_board.session_service.util;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.postgresql.PostgreSQLContainer;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@Tag("integration")
public abstract class AbstractIntegrationTest {

    @ServiceConnection
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    static {
        postgres.start();
    }
}
