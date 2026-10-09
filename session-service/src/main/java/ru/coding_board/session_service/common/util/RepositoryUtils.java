package ru.coding_board.session_service.common.util;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.coding_board.session_service.common.exception.ServiceEntityNotFoundException;

public final class RepositoryUtils {
    private RepositoryUtils() {}

    public static <T> T findOrThrow(
        JpaRepository<T, UUID> repository, UUID id, Class<T> entityType) {
        return repository.findById(id)
            .orElseThrow(() -> new ServiceEntityNotFoundException(entityType, id));
    }
}
