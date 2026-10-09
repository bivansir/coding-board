package ru.coding_board.session_service.common.exception;

import java.util.UUID;

import lombok.Getter;
import ru.coding_board.session_service.common.enums.SessionStatus;

@Getter
public class InvalidSessionStatusException extends RuntimeException {
    private final UUID sessionId;
    private final SessionStatus status;

    public InvalidSessionStatusException(UUID sessionId, SessionStatus status)
    {
        super(String.format("Сессия '%s' не может находится в состоянии '%s'",
            sessionId, status));
        this.sessionId = sessionId;
        this.status = status;
    }
}
