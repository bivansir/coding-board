package ru.coding_board.session_service.note;

import java.util.List;
import java.util.UUID;

import ru.coding_board.session_service.note.dto.NoteCreateRequest;
import ru.coding_board.session_service.note.dto.NoteListItemResponse;
import ru.coding_board.session_service.note.dto.NoteResponse;
import ru.coding_board.session_service.note.dto.NotesResponse;
import ru.coding_board.session_service.session.Session;

public final class NoteMapper {
    private NoteMapper() {}

    public static Note buildNote(NoteCreateRequest request, Session session) {
        return Note.builder()
            .session(session)
            .note(request.note())
            .time(request.time())
            .build();
    }

    public static NoteResponse buildResponse(Note note) {
        return NoteResponse.builder()
            .noteId(note.getNoteId())
            .sessionId(note.getSession().getSessionId())
            .note(note.getNote())
            .time(note.getTime())
            .build();
    }

    public static NotesResponse buildResponse(List<Note> notes, UUID sessionid) {
        return NotesResponse.builder()
            .sessionId(sessionid)
            .notes(notes.stream().map(n -> buildNoteListItem(n)).toList())
            .build();
    }

    private static NoteListItemResponse buildNoteListItem(Note note) {
        return NoteListItemResponse.builder()
            .noteId(note.getNoteId())
            .note(note.getNote())
            .time(note.getTime())
            .build();
    }
}
