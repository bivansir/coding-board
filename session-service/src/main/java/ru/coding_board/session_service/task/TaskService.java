package ru.coding_board.session_service.task;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ru.coding_board.session_service.common.util.RepositoryUtils;
import ru.coding_board.session_service.task.dto.TaskCreateRequest;
import ru.coding_board.session_service.task.dto.TaskFilterRequest;
import ru.coding_board.session_service.task.dto.TaskListItemResponse;
import ru.coding_board.session_service.task.dto.TaskPatchRequest;
import ru.coding_board.session_service.task.dto.TaskResponse;

@RequiredArgsConstructor
@Service
public class TaskService {
    private TaskRepository taskRepository;

    public TaskResponse getTaskById(UUID id) {
        return TaskMapper.buildResponse(getById(id));
    }

    public Page<TaskListItemResponse> filterTasks(TaskFilterRequest request, Pageable pageable) {
        return taskRepository.findByFilter(
            request.searchQuery(),
            request.category(),
            request.difficulties(),
            request.programmingLanguage(),
            request.filteredTaskIds(),
            pageable);
    }

    @Transactional
    public TaskResponse createTask(TaskCreateRequest request) {
        Task task = TaskMapper.buildTask(request);
        task.setIsActive(true);

        Task saved = taskRepository.save(task);

        return TaskMapper.buildResponse(saved);
    }

    @Transactional
    public TaskResponse patchTask(TaskPatchRequest request, UUID taskId) {
        Task task = getById(taskId);

        if (request.title() != null) task.setTitle(request.title());
        if (request.description() != null) task.setDescription(request.description());
        if (request.category() != null) task.setCategory(request.category());
        if (request.difficulty() != null) task.setDifficulty(request.difficulty());
        if (request.programmingLanguage() != null) task.setProgrammingLanguage(
            request.programmingLanguage());
        if (request.isActive() != null) task.setIsActive(request.isActive());

        Task saved = taskRepository.save(task);

        return TaskMapper.buildResponse(saved);
    }

    private Task getById(UUID id) {
        Task task = RepositoryUtils.findOrThrow(
            taskRepository, id, Task.class);

        return task;
    }
}

