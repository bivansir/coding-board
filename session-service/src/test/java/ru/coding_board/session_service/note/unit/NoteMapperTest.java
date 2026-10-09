package ru.coding_board.session_service.note.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import ru.coding_board.session_service.note.Note;
import ru.coding_board.session_service.note.NoteMapper;
import ru.coding_board.session_service.note.NoteTestDataBuilder;
import ru.coding_board.session_service.note.dto.NoteCreateRequest;
import ru.coding_board.session_service.note.dto.NoteResponse;
import ru.coding_board.session_service.note.dto.NotesResponse;
import ru.coding_board.session_service.session.Session;
import ru.coding_board.session_service.session.SessionTestDataBuilder;

public class NoteMapperTest {
    @Test
    void shouldBuildNote() {
        // given
        LocalDateTime time = LocalDateTime.now();
        Session session = SessionTestDataBuilder.aSessionCompletedWithId().build();

        NoteCreateRequest request = new NoteCreateRequest(
            "Test note",
            time
        );

        // when
        Note note = NoteMapper.buildNote(request, session);

        // then
        assertEquals(request.note(), note.getNote());
        assertEquals(session, note.getSession());
        assertEquals(request.time(), note.getTime());
    }

    @Test
    void shouldMapNoteToResponse() {
        // given
        Note note = NoteTestDataBuilder.aNote().build();

        // when
        NoteResponse response = NoteMapper.buildResponse(note);

        // then
        assertEquals(note.getNoteId(), response.noteId());
        assertEquals(note.getSession().getSessionId(), response.sessionId());
        assertEquals(note.getNote(), response.note());
        assertEquals(note.getTime(), response.time());
    }

    @Test
    void shouldBuildNotesResponse() {
        // given
        List<Note> notes = List.of(NoteTestDataBuilder.aNote().build());
        UUID sessionId = UUID.randomUUID();

        // when
        NotesResponse response = NoteMapper.buildResponse(notes, sessionId);

        // then
        assertEquals(sessionId, response.sessionId());
        assertEquals(notes.get(0).getNoteId(), response.notes().get(0).noteId());
        assertEquals(notes.get(0).getNote(), response.notes().get(0).note());
        assertEquals(notes.get(0).getTime(), response.notes().get(0).time());
    }
}
