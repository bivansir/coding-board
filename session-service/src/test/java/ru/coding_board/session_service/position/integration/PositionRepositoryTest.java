package ru.coding_board.session_service.position.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import ru.coding_board.session_service.position.PositionRepository;
import ru.coding_board.session_service.position.PositionTestDataBuilder;
import ru.coding_board.session_service.position.dto.PositionResponse;
import ru.coding_board.session_service.util.AbstractIntegrationTest;

public class PositionRepositoryTest extends AbstractIntegrationTest {
    @Autowired 
    private PositionRepository positionRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldProceedFuzzySearch() {
        // given
        entityManager.persist(PositionTestDataBuilder.aPosition().title("Java Разработчик").build());
        entityManager.flush();

        // when
        List<PositionResponse> found = positionRepository.searchByTitleAndIsActiveTrue(
            "разработчик java");

        // then
        assertEquals(1, found.size());
    }

    @Test
    void shouldProceedPrefixSearch() {
        // given
        entityManager.persist(PositionTestDataBuilder.aPosition().title("Разработчик Python").build());
        entityManager.persist(PositionTestDataBuilder.aPosition().title("Разработчик Java").build());
        entityManager.persist(PositionTestDataBuilder.aPosition().title("Менеджер Проектов").build());
        entityManager.flush();

        // when
        List<PositionResponse> found = positionRepository.searchByTitleAndIsActiveTrue(
            "разраб");

        // then
        assertEquals(2, found.size());
    }
}
