package ru.coding_board.session_service.session;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionCancellationRepository extends JpaRepository<SessionCancellation, UUID> {
    
}
