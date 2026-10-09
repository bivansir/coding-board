package ru.coding_board.session_service.position.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.coding_board.session_service.common.exception.ServiceEntityNotFoundException;
import ru.coding_board.session_service.position.Position;
import ru.coding_board.session_service.position.PositionMapper;
import ru.coding_board.session_service.position.PositionRepository;
import ru.coding_board.session_service.position.PositionService;
import ru.coding_board.session_service.position.PositionTestDataBuilder;
import ru.coding_board.session_service.position.dto.PositionCreateRequest;
import ru.coding_board.session_service.position.dto.PositionResponse;

@ExtendWith(MockitoExtension.class)
public class PositionServiceTest {
    @InjectMocks
    PositionService positionService;

    @Mock
    PositionRepository positionRepository;

    @Test
    void shouldReturnPositionById() {
        // given
        Position position = PositionTestDataBuilder.aPoisitionWithId().build();
        when(positionRepository.findById(position.getPositionId()))
            .thenReturn(Optional.of(position));

        // when
        PositionResponse result = positionService.getPositionById(position.getPositionId());

        // then
        assertEquals(PositionMapper.buildResponse(position), result);
    }

    @Test
    void shouldThrowWhenPositionNotFound() {
        // given
        UUID positionId = UUID.randomUUID();
        when(positionRepository.findById(positionId))
            .thenReturn(Optional.empty());

        // when then
        assertThrows(ServiceEntityNotFoundException.class,
            () -> positionService.getPositionById(positionId));
    }

    @Test
    void shouldUseFuzzySearchWithNormalizedTitleWhenLongEnough() {
        // given
        String title = "JAVA  РАЗРАБОТЧИК";

        when(positionRepository.searchByTitleAndIsActiveTrue(
            "java разработчик")).thenReturn(Collections.emptyList());

        // when
        positionService.findPositionsByTitle(title);

        // then
        verify(positionRepository).searchByTitleAndIsActiveTrue(
            "java разработчик");
    }

    @Test
    void shouldReturnEmptyListWhenqQueryIsTooShort() {
        // given
        String title = " ja";

        // when
        List<PositionResponse> response = positionService.findPositionsByTitle(title);

        // then
        verify(positionRepository, never()).searchByTitleAndIsActiveTrue(any());
        assertEquals(response, Collections.emptyList());
    }

    @Test
    void shouldFindPositionsByTitleAndReturnItsResultUnchanged() {
        // given
        String title = "JAVA  РАЗРАБОТЧИК";
        List<PositionResponse> positions = List.of(
            new PositionResponse(UUID.randomUUID(), "test")
        );

        when(positionRepository.searchByTitleAndIsActiveTrue(
            "java разработчик")).thenReturn(positions);

        // when
        List<PositionResponse> result = positionService.findPositionsByTitle(title);
        assertEquals(positions, result);
    }

    @Test
    void shouldSavePositionWithFieldsFromRequest() {
        // given
        PositionCreateRequest request = new PositionCreateRequest("Java Разработчик");
        
        when(positionRepository.save(any(Position.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        positionService.createPosition(request);

        // then
        ArgumentCaptor<Position> captor = ArgumentCaptor.forClass(Position.class);
        verify(positionRepository).save(captor.capture());
        Position saved = captor.getValue();

        assertEquals(request.title(), saved.getTitle());
    }

    @Test
    void shouldAlwaysSetNewPositionAsActive() {
        // given
        PositionCreateRequest request = new PositionCreateRequest("Java Разработчик");
        
        when(positionRepository.save(any(Position.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        positionService.createPosition(request);

        // then
        ArgumentCaptor<Position> captor = ArgumentCaptor.forClass(Position.class);
        verify(positionRepository).save(captor.capture());
        Position saved = captor.getValue();

        assertTrue(saved.getIsActive());
    }

    @Test
    void shouldReturnResponseBuiltFromSavedPosition() {
        // given
        PositionCreateRequest request = new PositionCreateRequest("Set in request");
        Position saved = PositionTestDataBuilder.aPoisitionWithId().title("Set in DB layer").build();

        when(positionRepository.save(any(Position.class))).thenReturn(saved);

        // when
        PositionResponse response = positionService.createPosition(request);

        // then
        assertEquals(saved.getTitle(), response.title());
        assertNotEquals(request.title(), response.title());
        assertEquals(saved.getPositionId(), response.positionId());
    }
    

    @Test
    void shouldDeactivatePositionById() {
        // given
        Position existing = PositionTestDataBuilder.aPoisitionWithId().build();

        when(positionRepository.findById(existing.getPositionId()))
            .thenReturn(Optional.of(existing));

        // when
        positionService.deactivatePosition(existing.getPositionId());

        // then
        assertEquals(false, existing.getIsActive());
    }
}
