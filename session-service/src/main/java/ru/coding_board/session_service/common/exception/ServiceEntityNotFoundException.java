package ru.coding_board.session_service.common.exception;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter 
public class ServiceEntityNotFoundException extends RuntimeException {
    private final Class<?> entityType;
    private final Map<String, Object> searchCriteria;

    public ServiceEntityNotFoundException(Class<?> entityType, UUID entityId) {
        this(entityType, Map.of("id", entityId));
    }

    public ServiceEntityNotFoundException(Class<?> entityType, Map<String, Object> searchCriteria) {
        super("Сущность %s не найдена по критериям: %s".formatted(
            entityType.getSimpleName(), formatCriteria(searchCriteria)));
        this.entityType = entityType;
        this.searchCriteria = searchCriteria;
    }

    private static String formatCriteria(Map<String, Object> criteria) {
        return criteria.entrySet().stream()
            .map(e -> "%s=%s".formatted(e.getKey(), e.getValue()))
            .collect(Collectors.joining(", "));
    }
}
