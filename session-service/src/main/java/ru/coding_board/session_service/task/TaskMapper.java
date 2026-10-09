package ru.coding_board.session_service.task;

import ru.coding_board.session_service.task.dto.TaskCreateRequest;
import ru.coding_board.session_service.task.dto.TaskResponse;

public class TaskMapper {
    private TaskMapper() {}

    public static TaskResponse buildResponse(Task task) {
        return TaskResponse.builder()
            .taskId(task.getTaskId())
            .title(task.getTitle())
            .description(task.getDescription())
            .difficulty(task.getDifficulty())
            .category(task.getCategory())
            .programmingLanguage(task.getProgrammingLanguage())
            .isActive(task.getIsActive())
            .build();
    }

    // isActive ставится в сервисе
    public static Task buildTask(TaskCreateRequest request) {
        return Task.builder()
            .title(request.title())
            .description(request.description())
            .difficulty(request.difficulty())
            .category(request.category())
            .programmingLanguage(request.programmingLanguage())
            .build();
    }

}
