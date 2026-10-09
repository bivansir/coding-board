package ru.coding_board.session_service.task.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ru.coding_board.session_service.common.enums.Category;
import ru.coding_board.session_service.common.enums.Difficulty;
import ru.coding_board.session_service.common.enums.ProgrammingLanguage;
import ru.coding_board.session_service.task.Task;
import ru.coding_board.session_service.task.TaskMapper;
import ru.coding_board.session_service.task.TaskTestDataBuilder;
import ru.coding_board.session_service.task.dto.TaskCreateRequest;
import ru.coding_board.session_service.task.dto.TaskResponse;

public class TaskMapperTest {
    @Test
    void shouldMapRequestToTask() {
        // given
        TaskCreateRequest request = new TaskCreateRequest(
            "Test title", "Test description", Difficulty.JUNIOR, 
            Category.ALGORITHMS, ProgrammingLanguage.JAVA
        );

        // when
        Task task = TaskMapper.buildTask(request);

        // then
        assertEquals(request.title(), task.getTitle());
        assertEquals(request.description(), task.getDescription());
        assertEquals(request.difficulty(), task.getDifficulty());
        assertEquals(request.category(), task.getCategory());
        assertEquals(request.programmingLanguage(), task.getProgrammingLanguage());
    }

    @Test
    void shouldMapTaskToResponse() {
        // given
        Task task = TaskTestDataBuilder.aTaskWithId().build();

        // when
        TaskResponse response = TaskMapper.buildResponse(task);

        // then
        assertEquals(task.getTitle(), response.title());
        assertEquals(task.getDescription(), response.description());
        assertEquals(task.getDifficulty(), response.difficulty());
        assertEquals(task.getCategory(), response.category());
        assertEquals(task.getProgrammingLanguage(), response.programmingLanguage());
    }
    
}
