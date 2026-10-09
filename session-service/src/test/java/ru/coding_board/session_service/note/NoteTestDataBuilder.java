package ru.coding_board.session_service.note;

import java.time.LocalDateTime;
import java.util.UUID;

import ru.coding_board.session_service.common.enums.SessionStatus;
import ru.coding_board.session_service.session.Session;

public class NoteTestDataBuilder {
    private NoteTestDataBuilder() {}

    public static Note.NoteBuilder aNote() {
        return Note.builder()
            .noteId(UUID.randomUUID())
            .session(minimalSession())
            .note("Test note")
            .time(LocalDateTime.now());
    }
    
    private static Session minimalSession() {
        return Session.builder()
            .sessionId(UUID.randomUUID())
            .sessionStatus(SessionStatus.ONGOING)
            .build();
    }
}
