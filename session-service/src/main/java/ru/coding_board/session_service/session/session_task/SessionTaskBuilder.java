package ru.coding_board.session_service.session.session_task;

import java.util.List;
import java.util.UUID;

import ru.coding_board.session_service.common.enums.ProgrammingLanguage;
import ru.coding_board.session_service.session.Session;
import ru.coding_board.session_service.session.session_task.dto.SessionTaskResponse;
import ru.coding_board.session_service.session.session_task.dto.SessionTasksResponse;
import ru.coding_board.session_service.task.Task;
import ru.coding_board.session_service.task.TaskMapper;

public class SessionTaskBuilder {
      public static SessionTask buildSessionTask(
        ProgrammingLanguage programmingLanguage,
        Session session,
        Task task) {
        return SessionTask.builder()
            .session(session)
            .task(task)
            .programmingLanguage(programmingLanguage)
            .build();
    }
    
    public static SessionTaskResponse buildResponse(SessionTask sessionTask) {
        return SessionTaskResponse.builder()
            .sessionTaskId(sessionTask.getSessionTaskId())
            .task(TaskMapper.buildResponse(sessionTask.getTask()))
            .programmingLanguage(sessionTask.getProgrammingLanguage())
            .build();
    }

    public static SessionTasksResponse buildResponse(
        List<SessionTaskResponse> sessionTasks, UUID sessionId) {
        return SessionTasksResponse.builder()
            .sessionId(sessionId)
            .sessionTasks(sessionTasks)
            .build();
    }
}
