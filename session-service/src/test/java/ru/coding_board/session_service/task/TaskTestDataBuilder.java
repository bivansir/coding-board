package ru.coding_board.session_service.task;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import ru.coding_board.session_service.common.enums.Category;
import ru.coding_board.session_service.common.enums.Difficulty;

public final class TaskTestDataBuilder {
    TaskTestDataBuilder() {};
    private static final AtomicInteger COUNTER = new AtomicInteger();

    public static Task.TaskBuilder aTaskWithId() {
        return Task.builder()
            .taskId(UUID.randomUUID())
            .title("Test title")
            .description("Test desc")
            .difficulty(Difficulty.JUNIOR)
            .category(Category.ALGORITHMS)
            .programmingLanguage(null)
            .isActive(true);
    }

    public static Task.TaskBuilder aTask() {
        int id = COUNTER.incrementAndGet();
        return Task.builder()
            .title("Test title-" + id)
            .description("Test desc")
            .difficulty(Difficulty.JUNIOR)
            .category(Category.ALGORITHMS)
            .programmingLanguage(null)
            .isActive(true);
    }
}
