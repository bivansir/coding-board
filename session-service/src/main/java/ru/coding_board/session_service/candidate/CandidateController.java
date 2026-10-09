package ru.coding_board.session_service.candidate;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
import ru.coding_board.session_service.candidate.dto.CandidateCreateRequest;
import ru.coding_board.session_service.candidate.dto.CandidateFilterRequest;
import ru.coding_board.session_service.candidate.dto.CandidateListItemResponse;
import ru.coding_board.session_service.candidate.dto.CandidatePatchRequest;
import ru.coding_board.session_service.candidate.dto.CandidateResponse;
import ru.coding_board.session_service.common.annotation.CommonErrorResponses;
import ru.coding_board.session_service.common.exception.ErrorResponse;

@RestController
@RequestMapping("/candidates")
@RequiredArgsConstructor
@Tag(name = "Candidate API", description = "API для работы с сущностью Candidate")
public class CandidateController {
    private final CandidateService candidateService;

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить стандартный Candidate View по Candidate ID"
    )
    @CommonErrorResponses 
    @ApiResponse(responseCode = "200", description = "Запрос выполнен",
        content = @Content(schema = @Schema(implementation = CandidateResponse.class))
    )
    public CandidateResponse getCandidateById(
        @Parameter(
            description = "Candidate ID"
        )
        @PathVariable UUID id
    ) {
        return candidateService.getCandidateById(id);
    }

    @GetMapping
    @Operation(
        summary = "Получить Candidate View's списка по запросу-фильтру"
    )
    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "400", description = "Невалидный запрос",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "200", description = "Запрос выполнен",
        content = @Content(schema = @Schema(
            implementation = Page.class, 
            subTypes = {CandidateListItemResponse.class}))
    )
    public Page<CandidateListItemResponse> filterCandidates(
        @Parameter(
            description = "Запрос-фильтр DTO"
        )
        @Valid CandidateFilterRequest request,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        return candidateService.filterCandidates(request, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Добавить Candidate в БД по запросу создания"
    )
    @CommonErrorResponses
    @ApiResponse(responseCode = "409", description = "Нарушена уникальность email",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(responseCode = "201", description = "Кандидат добавлен в БД",
        content = @Content(schema = @Schema(implementation = CandidateResponse.class))
    )
    public CandidateResponse createCandidate(
        @Parameter(
            description = "Запрос-создание DTO"
        )
        @RequestBody @Valid CandidateCreateRequest request
    ) {
        return candidateService.createCandidate(request);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Обновить поля Candidate в БД по патч-запросу"
    )
    @CommonErrorResponses
    @ApiResponse(responseCode = "409", description = "Нарушена уникальность email",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(responseCode = "200", description = "Запрос выполнен",
        content = @Content(schema = @Schema(implementation = CandidateResponse.class))
    )
    public CandidateResponse patchCandidate(
        @Parameter(
            description = "Candidate ID"
        )
        @PathVariable UUID id,
        @Parameter(
            description = "Запрос-патч DTO"
        )
        @RequestBody @Valid CandidatePatchRequest request
    ) {
        return candidateService.patchCandidate(id, request);
    }
}
