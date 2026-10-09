package ru.coding_board.session_service.position.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder 
public record PositionResponse(
    @Schema(description = "ID позиции", example = "a1a8c3d4-e5f6-7890-abcd-ef1234567890")
    UUID positionId,

    @Schema(description = "Имя позиции", example = "Java-разработчик")
    String title
) {}
