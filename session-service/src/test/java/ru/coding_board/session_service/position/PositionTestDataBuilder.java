package ru.coding_board.session_service.position;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public final class PositionTestDataBuilder {
    private PositionTestDataBuilder() {}
    private static final AtomicInteger COUNTER = new AtomicInteger();

    public static Position.PositionBuilder aPosition() {
        int id = COUNTER.incrementAndGet();
        return Position.builder()
        .title("Test title-" + id)
        .isActive(true);
    }

    public static Position.PositionBuilder aPoisitionWithId() {
        return Position.builder()
        .positionId(UUID.randomUUID())
        .title("Test title")
        .isActive(true);
    }
}
