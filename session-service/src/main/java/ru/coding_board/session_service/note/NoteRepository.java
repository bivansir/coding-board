package ru.coding_board.session_service.note;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, UUID> {
    List<Note> findAllBySessionSessionId(UUID sessionId);
    Optional<Note> findBySessionSessionIdAndNoteId(UUID sessionId, UUID noteId);
}
