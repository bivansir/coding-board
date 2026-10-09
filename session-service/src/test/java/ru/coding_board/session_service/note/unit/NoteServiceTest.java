package ru.coding_board.session_service.note.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.coding_board.session_service.common.enums.SessionStatus;
import ru.coding_board.session_service.common.exception.InvalidSessionStatusException;
import ru.coding_board.session_service.common.exception.ServiceEntityNotFoundException;
import ru.coding_board.session_service.note.Note;
import ru.coding_board.session_service.note.NoteMapper;
import ru.coding_board.session_service.note.NoteRepository;
import ru.coding_board.session_service.note.NoteService;
import ru.coding_board.session_service.note.NoteTestDataBuilder;
import ru.coding_board.session_service.note.dto.NoteCreateRequest;
import ru.coding_board.session_service.note.dto.NoteResponse;
import ru.coding_board.session_service.note.dto.NotesResponse;
import ru.coding_board.session_service.session.Session;
import ru.coding_board.session_service.session.SessionRepository;
import ru.coding_board.session_service.session.SessionTestDataBuilder;

@ExtendWith(MockitoExtension.class)
public class NoteServiceTest {
    @InjectMocks 
    NoteService noteService;

    @Mock
    NoteRepository noteRepository;

    @Mock
    SessionRepository sessionRepository;

    @Test 
    void shouldCreateNoteWithRequestFieldsUnchanged() {
        // given
        Session session = SessionTestDataBuilder.aSessionCompletedWithId().sessionStatus(SessionStatus.ONGOING)
            .build();
        NoteCreateRequest request = new NoteCreateRequest("Test note", LocalDateTime.now());

        when(sessionRepository.findById(session.getSessionId()))
            .thenReturn(Optional.of(session));
        when(noteRepository.save(any(Note.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        noteService.createNote(session.getSessionId(), request);

        // then
        ArgumentCaptor<Note> captor = ArgumentCaptor.forClass(Note.class);
        verify(noteRepository).save(captor.capture());
        Note saved = captor.getValue();

        assertEquals(session, saved.getSession());
        assertEquals(request.note(), saved.getNote());
        assertEquals(request.time(), saved.getTime());
    }

    @Test
    void shouldReturnResponseBuiltFromSavedNote() {
        // given
        Session session = SessionTestDataBuilder.aSessionCompletedWithId().sessionStatus(SessionStatus.ONGOING)
            .build();
        String note = "Set in Request";
        NoteCreateRequest request = new NoteCreateRequest(note, LocalDateTime.now());

        when(sessionRepository.findById(session.getSessionId())).thenReturn(Optional.of(session));

        Note saved = NoteTestDataBuilder.aNote().note("Set in DB layer").build();
        when(noteRepository.save(any(Note.class))).thenReturn(saved);

        // when
        NoteResponse response = noteService.createNote(session.getSessionId(), request);

        // then
        assertEquals(saved.getNote(), response.note());
        assertNotEquals(note, response.note());
        assertEquals(saved.getNoteId(), response.noteId());;
    }

    @Test
    void shouldThrowWhenSessionNotFoundOnNoteCreation() {
        // given
        UUID sessionId = UUID.randomUUID();
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        NoteCreateRequest request = new NoteCreateRequest(
            "Test note", LocalDateTime.now());

        // when then
        assertThrows(ServiceEntityNotFoundException.class,
            () -> noteService.createNote(sessionId, request));
    }

    @Test 
    void shouldThrowWhenSessionIsNotOngoingOnNoteCreation() {
        // given
        Session session = SessionTestDataBuilder.aSessionCompletedWithId().build();
        when(sessionRepository.findById(session.getSessionId())).thenReturn(Optional.of(session));

        NoteCreateRequest request = new NoteCreateRequest(
            "Test note", LocalDateTime.now());

        // when then
        assertThrows(InvalidSessionStatusException.class,
            () -> noteService.createNote(session.getSessionId(), request));
    }

    @Test
    void shouldGetNotesBySessionId() {
        // given
        UUID sessionId = UUID.randomUUID();
        List<Note> notes = List.of(
            NoteTestDataBuilder.aNote().note("First").build(),
            NoteTestDataBuilder.aNote().note("Second").build()
        );

        when(noteRepository.findAllBySessionSessionId(sessionId)).thenReturn(notes);

        // when
        NotesResponse result = noteService.getNotes(sessionId);

        // then
        assertEquals(NoteMapper.buildResponse(notes, sessionId), result);
    }

    @Test 
    void shouldDeleteNoteById() {
        // given
        Note note = NoteTestDataBuilder.aNote().build();
        when(noteRepository.findBySessionSessionIdAndNoteId(note.getSession().getSessionId(), note.getNoteId()))
            .thenReturn(Optional.of(note));

        // when
        noteService.deleteNote(note.getSession().getSessionId(), note.getNoteId());

        // then
        verify(noteRepository).delete(note);
    }

    @Test 
    void shouldThrowWhenNoteNotFoundOnDelete() {
        // given
        UUID noteId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        when(noteRepository.findBySessionSessionIdAndNoteId(
            sessionId, noteId)).thenReturn(Optional.empty());

        // when then
        assertThrows(ServiceEntityNotFoundException.class,
            () -> noteService.deleteNote(sessionId, noteId));

        verify(noteRepository, never()).delete(any());
    }

    @Test
    void shouldThrowWhenSessionIsNotOngoingOnNoteDelete() {
        // given
        Session session = SessionTestDataBuilder.aSessionCompletedWithId().build();
        Note note = NoteTestDataBuilder.aNote().session(session).build();

        when(noteRepository.findBySessionSessionIdAndNoteId(
            session.getSessionId(), note.getNoteId())).thenReturn(Optional.of(note));

        // when then
        assertThrows(InvalidSessionStatusException.class,
            () -> noteService.deleteNote(session.getSessionId(), note.getNoteId()));

        verify(noteRepository, never()).delete(any());
    }
}
