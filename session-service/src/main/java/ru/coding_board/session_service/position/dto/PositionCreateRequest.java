package ru.coding_board.session_service.position.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import ru.coding_board.session_service.common.util.StringStripper;

public record PositionCreateRequest(
    @NotBlank
    @Schema(description = "Имя позиции", example = "Java-разработчик")
    String title
) {
    public PositionCreateRequest {
        title = StringStripper.stripOrNull(title);
    }
}
