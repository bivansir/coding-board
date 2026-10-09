package ru.coding_board.session_service.session.session_task;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionTaskRepository extends JpaRepository<SessionTask, UUID> {
    List<SessionTask> findBySessionSessionId(UUID sessionId);
    long countBySessionTaskIdInAndSessionSessionId(List<UUID> sessionTaskId, UUID sessionId);
}
