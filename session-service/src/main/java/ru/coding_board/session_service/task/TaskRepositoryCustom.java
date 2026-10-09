package ru.coding_board.session_service.task;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ru.coding_board.session_service.common.enums.Category;
import ru.coding_board.session_service.common.enums.Difficulty;
import ru.coding_board.session_service.common.enums.ProgrammingLanguage;
import ru.coding_board.session_service.task.dto.TaskListItemResponse;

public interface TaskRepositoryCustom {
    Page<TaskListItemResponse> findByFilter(
        String searchQuery,
        Category category, 
        List<Difficulty> difficulties,
        ProgrammingLanguage programmingLanguage, 
        List<UUID> filteredTaskIds,
        Pageable pageable);
}
