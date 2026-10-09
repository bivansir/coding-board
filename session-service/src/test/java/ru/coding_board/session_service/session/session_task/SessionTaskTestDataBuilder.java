package ru.coding_board.session_service.session.session_task;

import java.util.UUID;

import ru.coding_board.session_service.common.enums.ProgrammingLanguage;
import ru.coding_board.session_service.session.Session;
import ru.coding_board.session_service.task.Task;

public final class SessionTaskTestDataBuilder {
    private SessionTaskTestDataBuilder() {};

    public static SessionTask.SessionTaskBuilder aSessionTask() {
        return SessionTask.builder()
            .sessionTaskId(UUID.randomUUID())
            .session(minimalSession())
            .task(minimalTask())
            .programmingLanguage(ProgrammingLanguage.JAVA);
    }

    private static Session minimalSession() {
        return Session.builder()
        .sessionId(UUID.randomUUID())
        .build();
    }

    private static Task minimalTask() {
        return Task.builder()
        .taskId(UUID.randomUUID())
        .build();
    }
}
