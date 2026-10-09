package ru.coding_board.session_service.session.session_task.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.coding_board.session_service.common.enums.ProgrammingLanguage;
import ru.coding_board.session_service.common.exception.ServiceEntityNotFoundException;
import ru.coding_board.session_service.session.Session;
import ru.coding_board.session_service.session.SessionRepository;
import ru.coding_board.session_service.session.SessionTestDataBuilder;
import ru.coding_board.session_service.session.session_task.SessionTask;
import ru.coding_board.session_service.session.session_task.SessionTaskBuilder;
import ru.coding_board.session_service.session.session_task.SessionTaskRepository;
import ru.coding_board.session_service.session.session_task.SessionTaskService;
import ru.coding_board.session_service.session.session_task.SessionTaskTestDataBuilder;
import ru.coding_board.session_service.session.session_task.dto.SessionTaskResponse;
import ru.coding_board.session_service.session.session_task.dto.SessionTasksCreateRequest;
import ru.coding_board.session_service.session.session_task.dto.SessionTasksPatchRequest;
import ru.coding_board.session_service.session.session_task.dto.SessionTasksResponse;
import ru.coding_board.session_service.task.Task;
import ru.coding_board.session_service.task.TaskMapper;
import ru.coding_board.session_service.task.TaskRepository;
import ru.coding_board.session_service.task.TaskTestDataBuilder;
import ru.coding_board.session_service.task.dto.TaskResponse;

@ExtendWith(MockitoExtension.class)
public class SessionTaskServiceTest {
    @InjectMocks
    SessionTaskService sessionTaskService;

    @Mock
    SessionTaskRepository sessionTaskRepository;

    @Mock
    SessionRepository sessionRepository;

    @Mock
    TaskRepository taskRepository;

    @Test
    void shouldGetSessionTasksBySessionId() {
        // given
        UUID sessionId = UUID.randomUUID();
        SessionTask sessionTask = SessionTaskTestDataBuilder.aSessionTask().build();
        when(sessionTaskRepository.findBySessionSessionId(sessionId)).thenReturn(List.of(sessionTask));

        // when
        SessionTasksResponse response = sessionTaskService.getSessionTasksBySessionId(sessionId);

        // then
        assertEquals(sessionId, response.sessionId());
        assertEquals(1, response.sessionTasks().size());
        assertEquals(SessionTaskBuilder.buildResponse(sessionTask),
            response.sessionTasks().get(0));
    }

    @Test
    void shouldCreateSessionTasksWithRequestFieldsUnchanged() {
        // given
        Session session = SessionTestDataBuilder.aSessionCompletedWithId().build();
        Task task = TaskTestDataBuilder.aTaskWithId().build();

        SessionTaskResponse item = SessionTaskResponse.builder()
            .task(TaskMapper.buildResponse(task))
            .programmingLanguage(ProgrammingLanguage.JAVA)
            .build();
        SessionTasksCreateRequest request = new SessionTasksCreateRequest(List.of(item));

        when(sessionRepository.findById(session.getSessionId()))
            .thenReturn(Optional.of(session));
        when(taskRepository.findById(task.getTaskId()))
            .thenReturn(Optional.of(task));
        when(sessionTaskRepository.saveAll(anyList()))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        sessionTaskService.createSessionTasks(session.getSessionId(), request);

        // then
        ArgumentCaptor<List<SessionTask>> captor = ArgumentCaptor.captor();
        verify(sessionTaskRepository).saveAll(captor.capture());

        List<SessionTask> saved = captor.getValue();
        assertEquals(1, saved.size());
        assertEquals(session, saved.get(0).getSession());
        assertEquals(task, saved.get(0).getTask());
        assertEquals(ProgrammingLanguage.JAVA, saved.get(0).getProgrammingLanguage());
    }

    @Test
    void shouldThrowWhenSessionNotFound() {
        // given
        UUID sessionId = UUID.randomUUID();
        SessionTasksCreateRequest request = new SessionTasksCreateRequest(List.of());
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        // when then
        assertThrows(ServiceEntityNotFoundException.class,
            () -> sessionTaskService.createSessionTasks(sessionId, request));

        verify(sessionTaskRepository, never()).saveAll(any());
    }

    @Test
    void shouldThrowWhenTaskNotFound() {
        // given
        Session session = SessionTestDataBuilder.aSessionCompletedWithId().build();
        UUID taskId = UUID.randomUUID();

        SessionTaskResponse item = SessionTaskResponse.builder()
            .task(new TaskResponse(
                taskId, null, null, null, null, null, null))
            .programmingLanguage(ProgrammingLanguage.JAVA)
            .build();
        SessionTasksCreateRequest request = new SessionTasksCreateRequest(List.of(item));

        when(sessionRepository.findById(session.getSessionId()))
            .thenReturn(Optional.of(session));
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // when then
        assertThrows(ServiceEntityNotFoundException.class,
            () -> sessionTaskService.createSessionTasks(session.getSessionId(), request));

        verify(sessionTaskRepository, never()).saveAll(any());
    }

    @Test
    void shouldReturnResponseBuiltFromSavedSessionTasks() {
        // given
        Session session = SessionTestDataBuilder.aSessionCompletedWithId().build();
        Task task = TaskTestDataBuilder.aTaskWithId().build();

        SessionTaskResponse item = SessionTaskResponse.builder()
            .task(TaskMapper.buildResponse(task))
            .programmingLanguage(ProgrammingLanguage.JAVA)
            .build();
        SessionTasksCreateRequest request = new SessionTasksCreateRequest(List.of(item));

        when(sessionRepository.findById(session.getSessionId()))
            .thenReturn(Optional.of(session));
        when(taskRepository.findById(task.getTaskId())).thenReturn(Optional.of(task));

    
        SessionTask savedFromDb = SessionTaskTestDataBuilder.aSessionTask().programmingLanguage(
            ProgrammingLanguage.PYTHON).build();
        when(sessionTaskRepository.saveAll(anyList())).thenReturn(List.of(savedFromDb));

        // when
        SessionTasksResponse response = sessionTaskService.createSessionTasks(session.getSessionId(), request);

        // then
        assertEquals(ProgrammingLanguage.PYTHON, 
            response.sessionTasks().get(0).programmingLanguage());
        assertNotEquals(ProgrammingLanguage.JAVA, 
            response.sessionTasks().get(0).programmingLanguage());
    }

    @Test
    void shouldDeleteSessionTasks() {
        // given
        UUID sessionId = UUID.randomUUID();
        List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());

        when(sessionTaskRepository.countBySessionTaskIdInAndSessionSessionId(ids, sessionId))
            .thenReturn(Long.valueOf(2));

        // when
        sessionTaskService.deleteSessionTasks(sessionId, ids);

        // then
        verify(sessionTaskRepository).deleteAllByIdInBatch(ids);
    }

    @Test
    void shouldThrowWhenOneOfTheSessionTasksNotExistsWhenTryingToDelete() {
        // given
        UUID sessionId = UUID.randomUUID();
        List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());

        when(sessionTaskRepository.countBySessionTaskIdInAndSessionSessionId(ids, sessionId))
            .thenReturn(Long.valueOf(1));

        // when then
        assertThrows(ServiceEntityNotFoundException.class,
            () -> sessionTaskService.deleteSessionTasks(sessionId, ids));

        verify(sessionTaskRepository, never()).deleteAllByIdInBatch(any());
    }

    @Test
    void shouldThrowWhenSessionTaskNotFound() {
        // given
        UUID sessionTaskId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        SessionTasksPatchRequest request =
            new SessionTasksPatchRequest(sessionTaskId, ProgrammingLanguage.JAVA);
        when(sessionTaskRepository.findById(sessionTaskId)).thenReturn(Optional.empty());

        // when then
        assertThrows(ServiceEntityNotFoundException.class,
            () -> sessionTaskService.patchSessionTasks(sessionId, List.of(request)));

        verify(sessionTaskRepository, never()).saveAll(any());
    }

    @Test
    void shouldUpdateSessionTaskProgrammingLanguage() {
        // given
        SessionTask existing = SessionTaskTestDataBuilder.aSessionTask().programmingLanguage(
            ProgrammingLanguage.JAVA).build();
        UUID sessionId = existing.getSession().getSessionId();
        SessionTasksPatchRequest request = new SessionTasksPatchRequest(
            existing.getSessionTaskId(), ProgrammingLanguage.PYTHON);

        when(sessionTaskRepository.findById(existing.getSessionTaskId()))
            .thenReturn(Optional.of(existing));

        // when
        SessionTasksResponse response = sessionTaskService.patchSessionTasks(
            sessionId, List.of(request));

        // then
        assertEquals(ProgrammingLanguage.PYTHON, existing.getProgrammingLanguage());
        verify(sessionTaskRepository).saveAll(List.of(existing));
        assertEquals(existing.getSession().getSessionId(), response.sessionId());
        assertEquals(1, response.sessionTasks().size());
    }

    @Test
    void shouldThrowWhenSessionTaskSessionIdNotEqualsGivenSessionId() {
        // given
        SessionTask existing = SessionTaskTestDataBuilder.aSessionTask().programmingLanguage(
            ProgrammingLanguage.JAVA).build();
        UUID sessionId = UUID.randomUUID();
        SessionTasksPatchRequest request = new SessionTasksPatchRequest(
            existing.getSessionTaskId(), ProgrammingLanguage.PYTHON);

        when(sessionTaskRepository.findById(existing.getSessionTaskId()))
            .thenReturn(Optional.of(existing));

        // when then
        assertThrows(ServiceEntityNotFoundException.class,
            () -> sessionTaskService.patchSessionTasks(sessionId, List.of(request)));

        verify(sessionTaskRepository, never()).saveAll(any());
    }
}
