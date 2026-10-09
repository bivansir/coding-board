package ru.coding_board.session_service.note;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ru.coding_board.session_service.common.enums.SessionStatus;
import ru.coding_board.session_service.common.exception.ServiceEntityNotFoundException;
import ru.coding_board.session_service.common.exception.InvalidSessionStatusException;
import ru.coding_board.session_service.common.util.RepositoryUtils;
import ru.coding_board.session_service.note.dto.NoteCreateRequest;
import ru.coding_board.session_service.note.dto.NoteResponse;
import ru.coding_board.session_service.note.dto.NotesResponse;
import ru.coding_board.session_service.session.Session;
import ru.coding_board.session_service.session.SessionRepository;

@RequiredArgsConstructor
@Service
public class NoteService {
    private NoteRepository noteRepository;
    private SessionRepository sessionRepository;

    @Transactional
    public NoteResponse createNote(UUID sessionId, NoteCreateRequest request) {
        Session session = getSessionById(sessionId);

        if (session.getSessionStatus() != SessionStatus.ONGOING) {
            throw new InvalidSessionStatusException(session.getSessionId(), session.getSessionStatus());
        };

        Note saved = noteRepository.save(NoteMapper.buildNote(request, session));

        return NoteMapper.buildResponse(saved);
    }

    public NotesResponse getNotes(UUID sessionId) {
        return NoteMapper.buildResponse(
            noteRepository.findAllBySessionSessionId(sessionId), sessionId);
    }

    @Transactional 
    public void deleteNote(UUID sessionId, UUID noteId) {
        Note note = getBySessionIdAndNoteId(sessionId, noteId);

        Session session = note.getSession();
        if (session.getSessionStatus() != SessionStatus.ONGOING) {
            throw new InvalidSessionStatusException(session.getSessionId(), session.getSessionStatus());
        };
        
        noteRepository.delete(note);
    }

    private Note getBySessionIdAndNoteId(UUID sessionId, UUID noteId) {
        return noteRepository.findBySessionSessionIdAndNoteId(sessionId, noteId)
            .orElseThrow(() -> new ServiceEntityNotFoundException(Note.class,
                Map.of("sessionId", sessionId, "noteId", noteId)));
    }

    private Session getSessionById(UUID sessionId) {
        Session session = RepositoryUtils.findOrThrow(
            sessionRepository, sessionId, Session.class);
        
        return session;
    }
    
}
