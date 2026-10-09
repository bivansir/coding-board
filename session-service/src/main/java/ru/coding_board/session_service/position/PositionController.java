package ru.coding_board.session_service.position;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.coding_board.session_service.common.annotation.CommonErrorResponses;
import ru.coding_board.session_service.common.exception.ErrorResponse;
import ru.coding_board.session_service.position.dto.PositionCreateRequest;
import ru.coding_board.session_service.position.dto.PositionResponse;

@RestController
@RequestMapping("/positions")
@RequiredArgsConstructor
@Tag(name = "Position API", description = "API для работы с сущностью Position")
public class PositionController {
    private final PositionService positionService;

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить стандартный Position View по Position ID"
    )
    @CommonErrorResponses
    @ApiResponse(responseCode = "200", description = "Запрос выполнен",
        content = @Content(schema = @Schema(implementation = PositionResponse.class))
    )
    public PositionResponse getPositionById(
        @Parameter(
            description = "Position ID"
        )
        @PathVariable UUID id
    ) {
        return positionService.getPositionById(id);
    }

    @GetMapping
    @Operation(
            summary = "Получить Position View's по названию позиции (лайв-поиск)"
    )
    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "400", description = "Невалидный запрос",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "200", description = "Запрос выполнен",
        content = @Content(schema = @Schema(
            implementation = List.class, 
            subTypes = {PositionResponse.class}
        ))
    )
    public List<PositionResponse> findPositionsByTitle(
        @Parameter(
            description = "Название позиции"
        )
        @RequestParam String title
    ) {
        return positionService.findPositionsByTitle(title);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Добавить Position в БД по запросу-созданию"
    )
    @CommonErrorResponses
    @ApiResponse(responseCode = "409", description = "Нарушена уникальность title",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(responseCode = "201", description = "Position добавлен в БД",
        content = @Content(schema = @Schema(implementation = PositionResponse.class))
    )
    public PositionResponse createPosition(
        @Parameter(
            description = "Запрос-создание DTO"
        )
        @Valid @RequestBody PositionCreateRequest request
    ) {
        return positionService.createPosition(request);
    }

    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Скрыть Position по ID"
    )
    @CommonErrorResponses
    @ApiResponse(responseCode = "204", description = "Position скрыта")
    public void deactivatePosition(
        @Parameter(
            description = "Position ID"
        )
        @PathVariable UUID id
    ) {
        positionService.deactivatePosition(id);
    }
}
