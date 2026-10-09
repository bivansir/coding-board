package ru.coding_board.session_service.session.session_task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ru.coding_board.session_service.common.enums.ProgrammingLanguage;
import ru.coding_board.session_service.common.exception.ServiceEntityNotFoundException;
import ru.coding_board.session_service.common.util.RepositoryUtils;
import ru.coding_board.session_service.session.Session;
import ru.coding_board.session_service.session.SessionRepository;
import ru.coding_board.session_service.session.session_task.dto.SessionTaskResponse;
import ru.coding_board.session_service.session.session_task.dto.SessionTasksCreateRequest;
import ru.coding_board.session_service.session.session_task.dto.SessionTasksPatchRequest;
import ru.coding_board.session_service.session.session_task.dto.SessionTasksResponse;
import ru.coding_board.session_service.task.Task;
import ru.coding_board.session_service.task.TaskRepository;

@RequiredArgsConstructor
@Service
public class SessionTaskService {
    private final SessionTaskRepository sessionTaskRepository;
    private final SessionRepository sessionRepository;
    private final TaskRepository taskRepository;

    public SessionTasksResponse getSessionTasksBySessionId(UUID sessionId) {
        List<SessionTaskResponse> sessionTaskResponses = sessionTaskRepository
            .findBySessionSessionId(sessionId)
            .stream().map(st -> SessionTaskBuilder.buildResponse(st)).toList();

        return SessionTaskBuilder.buildResponse(sessionTaskResponses, sessionId);
    }

    @Transactional
    public SessionTasksResponse createSessionTasks(UUID sessionid,
            SessionTasksCreateRequest request) {
        Session session = getSessionById(sessionid);

        List<SessionTask> sessionTasks = new ArrayList<>();
        
        for (SessionTaskResponse sessionTask : request.sessionTasks()) {
            Task task = getTaskById(sessionTask.task().taskId());

           sessionTasks.add(SessionTaskBuilder.buildSessionTask(
                sessionTask.programmingLanguage(), session, task));
        }
        List<SessionTask> sessionTasksSaved = sessionTaskRepository.saveAll(sessionTasks);

        return SessionTaskBuilder.buildResponse(
            sessionTasksSaved.stream().map(st -> SessionTaskBuilder.buildResponse(st)).toList(),
            sessionid);
    }

    @Transactional
    public void deleteSessionTasks(UUID sessionId, List<UUID> sessionTaskIds) {
        long matchingCount = sessionTaskRepository
            .countBySessionTaskIdInAndSessionSessionId(sessionTaskIds, sessionId);

        if (matchingCount != sessionTaskIds.size()) {
            throw new ServiceEntityNotFoundException(SessionTask.class,
                Map.of("sessionId", sessionId, "sessionTaskIds", sessionTaskIds));
        }
        sessionTaskRepository.deleteAllByIdInBatch(sessionTaskIds);
    }

    @Transactional
    public SessionTasksResponse patchSessionTasks(UUID sessionId,
        List<SessionTasksPatchRequest> requestList) {
        List<SessionTask> sessionTasksToUpdate = new ArrayList<>();
        for (SessionTasksPatchRequest request : requestList) {
            SessionTask sessionTask = getById(request.sessionTaskId());

            if (!sessionTask.getSession().getSessionId().equals(sessionId)) {
            throw new ServiceEntityNotFoundException(SessionTask.class,
                Map.of("sessionId", sessionId,
                 "sessionTaskId", sessionTask.getSessionTaskId()));
            }

            ProgrammingLanguage programmingLanguage = request.programmingLanguage();
            if (programmingLanguage != null) {
                sessionTask.setProgrammingLanguage(programmingLanguage);
            }
            sessionTasksToUpdate.add(sessionTask);
        }
        sessionTaskRepository.saveAll(sessionTasksToUpdate);

        return SessionTaskBuilder.buildResponse(
            sessionTasksToUpdate.stream().map(
                st -> SessionTaskBuilder.buildResponse(st)).toList(),
            // у всех одинаковый sessionId
            sessionTasksToUpdate.get(0).getSession().getSessionId());

    }
    
    private SessionTask getById(UUID id) {
        SessionTask sesionTask = sessionTaskRepository.findById(id)
            .orElseThrow(() -> new ServiceEntityNotFoundException(
                SessionTask.class, id));
        
        return sesionTask;
    }

    private Session getSessionById(UUID id) {
        Session session = RepositoryUtils.findOrThrow(
            sessionRepository, id, Session.class);
        
        return session;
    }

    private Task getTaskById(UUID id) {
        Task task = RepositoryUtils.findOrThrow(
            taskRepository, id, Task.class);
        
        return task;
    }
}
