package ru.coding_board.session_service.task.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import jakarta.persistence.EntityManager;
import ru.coding_board.session_service.common.enums.Category;
import ru.coding_board.session_service.common.enums.Difficulty;
import ru.coding_board.session_service.common.enums.ProgrammingLanguage;
import ru.coding_board.session_service.task.Task;
import ru.coding_board.session_service.task.TaskRepositoryCustomImpl;
import ru.coding_board.session_service.task.TaskTestDataBuilder;
import ru.coding_board.session_service.task.dto.TaskListItemResponse;
import ru.coding_board.session_service.util.AbstractIntegrationTest;

public class TaskRepositoryCustomImplTest extends AbstractIntegrationTest {
    @Autowired
    TaskRepositoryCustomImpl taskRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    void shouldUseVectorSearchWithEnglishLanguage() {
        // given
        Task taskFound = TaskTestDataBuilder.aTask().title("Two sum").build();

        entityManager.persist(taskFound);
        entityManager.flush();

        // when
        Page<TaskListItemResponse> found = taskRepository.findByFilter(
            "sum", null, null,
            null, null, PageRequest.of(0, 10));

        // then
        assertEquals(1, found.getContent().size());
    }

    @Test
    void shouldUseVectorSearchWithRussianLanguage() {
        // given
        Task taskFound = TaskTestDataBuilder.aTask().title("Сумма двух чисел").build();

        entityManager.persist(taskFound);
        entityManager.flush();

        // when
        Page<TaskListItemResponse> found = taskRepository.findByFilter(
            "сумма", null, null,
            null, null, PageRequest.of(0, 10));

        // then
        assertEquals(1, found.getContent().size());
    }

    @Test
    void shouldUseVectorSearchWithEnglishAndRussianLanguage() {
        // given
        Task taskFound = TaskTestDataBuilder.aTask().title("Реализация QuickSearch").build();

        entityManager.persist(taskFound);
        entityManager.flush();

        // when
        Page<TaskListItemResponse> found = taskRepository.findByFilter(
            "quicksearch", null, null,
            null, null, PageRequest.of(0, 10));

        // then
        assertEquals(1, found.getContent().size());
    }

    @Test
    void shouldFilterByCategory() {
        // given
        Task taskFound = TaskTestDataBuilder.aTask().category(Category.ALGORITHMS).build();
        Task taskNotFound = TaskTestDataBuilder.aTask().category(Category.OOP).build();

        entityManager.persist(taskFound);
        entityManager.persist(taskNotFound);
        entityManager.flush();

        // when
        Page<TaskListItemResponse> found = taskRepository.findByFilter(
            null, Category.ALGORITHMS, null,
            null, null, PageRequest.of(0, 10));

        // then
        assertTrue(found.stream().anyMatch(t -> taskFound.getCategory().equals(t.category())));
    }

    @Test
    void shouldFilterByDifficulties() {
         // given
        Task taskFound1 = TaskTestDataBuilder.aTask().difficulty(Difficulty.JUNIOR).build();
        Task taskFound2 = TaskTestDataBuilder.aTask().difficulty(Difficulty.MIDDLE).build();
        Task taskNotFound = TaskTestDataBuilder.aTask().difficulty(Difficulty.SENIOR).build();

        entityManager.persist(taskFound1);
        entityManager.persist(taskFound2);
        entityManager.persist(taskNotFound);
        entityManager.flush();

        // when
        Page<TaskListItemResponse> found = taskRepository.findByFilter(
            null, null, List.of(Difficulty.JUNIOR, Difficulty.MIDDLE),
            null, null, PageRequest.of(0, 10));

        // then
        assertEquals(2, found.getContent().size());
        assertFalse(found.stream().anyMatch(t -> taskNotFound.getTaskId().equals(t.taskId())));
    }

    @Test
    void shouldFilterByProgrammingLanguage() {
        // given
        Task taskFound = TaskTestDataBuilder.aTask().programmingLanguage(
            ProgrammingLanguage.GO).build();
        Task taskNotFound = TaskTestDataBuilder.aTask().programmingLanguage(
            ProgrammingLanguage.JAVA).build();

        entityManager.persist(taskFound);
        entityManager.persist(taskNotFound);
        entityManager.flush();

        // when
        Page<TaskListItemResponse> found = taskRepository.findByFilter(
            null, null, null,
            ProgrammingLanguage.GO, null, PageRequest.of(0, 10));

        // then
        assertTrue(found.stream().anyMatch(t -> taskFound.getProgrammingLanguage()
            .equals(t.programmingLanguage())));
    }

    @Test
    void shouldFilterTasksWithGivenIds() {
        // given
        Task taskFound = TaskTestDataBuilder.aTask().build();
        Task taskFiltered = TaskTestDataBuilder.aTask().build();

        entityManager.persist(taskFound);
        entityManager.persist(taskFiltered);
        entityManager.flush();

        // when
        Page<TaskListItemResponse> found = taskRepository.findByFilter(
            null, null, null,
        null, List.of(taskFiltered.getTaskId()), PageRequest.of(0, 10));

        // then
        assertTrue(found.stream().anyMatch(t -> taskFound.getTaskId().equals(t.taskId())));
    }

    @Test
    void shouldCombineFilters() {
        // given
        Task taskFound = TaskTestDataBuilder.aTask().difficulty(
            Difficulty.JUNIOR).category(Category.ALGORITHMS).programmingLanguage(ProgrammingLanguage.JAVA)
            .build();
        Task taskNotFound = TaskTestDataBuilder.aTask().difficulty(
            Difficulty.MIDDLE).category(Category.OOP).programmingLanguage(ProgrammingLanguage.JAVA)
            .build();

        entityManager.persist(taskFound);
        entityManager.persist(taskNotFound);
        entityManager.flush();

        // when
        Page<TaskListItemResponse> found = taskRepository.findByFilter(
            null,
            Category.ALGORITHMS, 
            List.of(Difficulty.JUNIOR, Difficulty.MIDDLE),
            ProgrammingLanguage.JAVA,
            List.of(UUID.randomUUID()), PageRequest.of(0, 10));

        // then
        assertTrue(found.stream().anyMatch(t -> taskFound.getTaskId().equals(t.taskId())));
    }

    @Test
    void shouldReturnAllIfFiltersIsNull() {
        // given
        Task taskFound = TaskTestDataBuilder.aTask().build();

        entityManager.persist(taskFound);
        entityManager.flush();

        // when
        Page<TaskListItemResponse> found = taskRepository.findByFilter(
            null,
            null, 
            null,
            null,
            null, PageRequest.of(0, 10));

        // then
        assertEquals(1, found.getContent().size());
    }

    @Test
    void ifSearchIsGivenShouldOrderByRank() {
        // given
        Task veryRelevant = TaskTestDataBuilder.aTask()
            .title("Реализация дерева").build();

        Task lessRelevant = TaskTestDataBuilder.aTask()
            .title("Реализация дерева бинарного поиска с балансировкой и множеством других деталей")
            .build();

        entityManager.persist(veryRelevant);
        entityManager.persist(lessRelevant);
        entityManager.flush();

        // when
        Page<TaskListItemResponse> found = taskRepository.findByFilter(
            "Реализация дерева", null, null, null, null, PageRequest.of(0, 10));

        // then
        assertEquals(veryRelevant.getTaskId(), found.getContent().get(0).taskId());
    }

    @Test
    void ifSearchIsNotGivenShouldReturnStableOrder() {
        // given
        Task taskFound1 = TaskTestDataBuilder.aTask().build();
        Task taskFound2 = TaskTestDataBuilder.aTask().build();

        entityManager.persist(taskFound1);
        entityManager.persist(taskFound2);
        entityManager.flush();

        // when
        Page<TaskListItemResponse> firstCall = taskRepository.findByFilter(
            null, null, null, null, null, PageRequest.of(0, 10));
        Page<TaskListItemResponse> secondCall = taskRepository.findByFilter(
            null, null, null, null, null, PageRequest.of(0, 10));

        // then
        assertEquals(
            firstCall.getContent().stream().map((TaskListItemResponse item) -> item.taskId()).toList(),
            secondCall.getContent().stream().map((TaskListItemResponse item) -> item.taskId()).toList()
        );
    }

    @Test
    void shouldRespsectPageable() {
        // given
        for (int i = 0; i < 5; i++) {
            entityManager.persist(TaskTestDataBuilder.aTask().build());
        }
        entityManager.flush();

        // when
        Page<TaskListItemResponse> firstPage = taskRepository.findByFilter(null,
            null, null,null,
             null, PageRequest.of(0, 2));
        Page<TaskListItemResponse> secondPage = taskRepository.findByFilter(null,
            null, null,null,
             null, PageRequest.of(1,2));

        // then
        assertEquals(2, firstPage.getContent().size());
        assertEquals(2, secondPage.getContent().size());
        assertEquals(5, firstPage.getTotalElements());
        assertNotEquals(
            firstPage.getContent().get(0).taskId(),
            secondPage.getContent().get(0).taskId()
        );
    }
}
