package ru.coding_board.session_service.feedback;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
    Optional<Feedback> findBySessionSessionId(UUID sesionId);
}
