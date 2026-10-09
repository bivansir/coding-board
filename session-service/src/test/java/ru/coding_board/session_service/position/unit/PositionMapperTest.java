package ru.coding_board.session_service.position.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ru.coding_board.session_service.position.Position;
import ru.coding_board.session_service.position.PositionMapper;
import ru.coding_board.session_service.position.PositionTestDataBuilder;
import ru.coding_board.session_service.position.dto.PositionCreateRequest;
import ru.coding_board.session_service.position.dto.PositionResponse;

public class PositionMapperTest {
    @Test 
    void shouldMapPositionToResponse() {
        // given
        Position position = PositionTestDataBuilder.aPoisitionWithId().build();

        // when
        PositionResponse response = PositionMapper.buildResponse(position);

        // then
        assertEquals(position.getPositionId(), response.positionId());
        assertEquals(position.getTitle(), response.title());
    }

    @Test 
    void shouldMapCreateRequestToPositions() {
        // given
        PositionCreateRequest request = new PositionCreateRequest("test");

        // when
        Position position = PositionMapper.buildPosition(request);

        // then
        assertEquals(request.title(), position.getTitle());
    }
}
