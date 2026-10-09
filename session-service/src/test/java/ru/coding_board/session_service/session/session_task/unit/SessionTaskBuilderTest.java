package ru.coding_board.session_service.session.session_task.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import ru.coding_board.session_service.common.enums.ProgrammingLanguage;
import ru.coding_board.session_service.session.Session;
import ru.coding_board.session_service.session.SessionTestDataBuilder;
import ru.coding_board.session_service.session.session_task.SessionTask;
import ru.coding_board.session_service.session.session_task.SessionTaskBuilder;
import ru.coding_board.session_service.session.session_task.SessionTaskTestDataBuilder;
import ru.coding_board.session_service.session.session_task.dto.SessionTaskResponse;
import ru.coding_board.session_service.session.session_task.dto.SessionTasksResponse;
import ru.coding_board.session_service.task.Task;
import ru.coding_board.session_service.task.TaskMapper;
import ru.coding_board.session_service.task.TaskTestDataBuilder;

public class SessionTaskBuilderTest {
    @Test
    void shouldBuildSessionTask() {
        // given
        Session session = SessionTestDataBuilder.aSessionCompletedWithId().build();
        Task task = TaskTestDataBuilder.aTaskWithId().build();
        ProgrammingLanguage programmingLanguage = ProgrammingLanguage.JAVA;

        // when
        SessionTask result = SessionTaskBuilder.buildSessionTask(
            programmingLanguage, session, task);

        // then
        assertEquals(session, result.getSession());
        assertEquals(task, result.getTask());
        assertEquals(programmingLanguage, result.getProgrammingLanguage());
    }

    @Test
    void shouldBuildSessionTaskResponse() {
        // given
        SessionTask sessionTask = SessionTaskTestDataBuilder.aSessionTask().programmingLanguage(
            ProgrammingLanguage.PYTHON).build();

        // when
        SessionTaskResponse response = SessionTaskBuilder.buildResponse(sessionTask);

        // then
        assertEquals(sessionTask.getSessionTaskId(), response.sessionTaskId());
        assertEquals(TaskMapper.buildResponse(sessionTask.getTask()), response.task());
        assertEquals(ProgrammingLanguage.PYTHON, response.programmingLanguage());
    }

    @Test
    void shouldBuildSessionTasksResponse() {
        // given
        UUID sessionId = UUID.randomUUID();
        SessionTaskResponse item1 = SessionTaskResponse.builder()
            .sessionTaskId(UUID.randomUUID())
            .programmingLanguage(ProgrammingLanguage.JAVA)
            .build();
        SessionTaskResponse item2 = SessionTaskResponse.builder()
            .sessionTaskId(UUID.randomUUID())
            .programmingLanguage(ProgrammingLanguage.PYTHON)
            .build();
        List<SessionTaskResponse> sessionTasks = List.of(item1, item2);

        // when
        SessionTasksResponse response = SessionTaskBuilder.buildResponse(sessionTasks, sessionId);

        // then
        assertEquals(sessionId, response.sessionId());
        assertEquals(sessionTasks, response.sessionTasks());
    }

}
