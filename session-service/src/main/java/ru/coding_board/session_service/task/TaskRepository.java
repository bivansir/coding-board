package ru.coding_board.session_service.task;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, UUID>, TaskRepositoryCustom {
}
