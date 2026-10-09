package ru.coding_board.session_service.common.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
    int status,
    String errorCode,
    String message,
    String path,
    LocalDateTime timestamp
) {
    
}
