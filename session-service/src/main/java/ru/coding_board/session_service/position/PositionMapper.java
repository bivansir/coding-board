package ru.coding_board.session_service.position;

import ru.coding_board.session_service.position.dto.PositionCreateRequest;
import ru.coding_board.session_service.position.dto.PositionResponse;

public class PositionMapper {
    private PositionMapper() {}

    //isActive сеттится в сервисе
    public static Position buildPosition(PositionCreateRequest request) {
        return Position.builder()
            .title(request.title())
            .build();
    }

    public static PositionResponse buildResponse(Position position) {
        return PositionResponse.builder()
            .positionId(position.getPositionId())
            .title(position.getTitle())
            .build();
    }
}
