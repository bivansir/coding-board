package ru.coding_board.session_service.position;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ru.coding_board.session_service.common.util.RepositoryUtils;
import ru.coding_board.session_service.position.dto.PositionCreateRequest;
import ru.coding_board.session_service.position.dto.PositionResponse;

@RequiredArgsConstructor
@Service
public class PositionService {
    private static final int MIN_QUERY_LENGTH = 3;
    private final PositionRepository positionRepository;

    public PositionResponse getPositionById(UUID positionId) {
        return PositionMapper.buildResponse(getById(positionId));
    }

    public List<PositionResponse> findPositionsByTitle(String title) {
        String query = normalizeSearchQuery(title);

        if (query.length() <= MIN_QUERY_LENGTH) return Collections.emptyList();

        return positionRepository.searchByTitleAndIsActiveTrue(query);
    }

    @Transactional
    public PositionResponse createPosition(PositionCreateRequest request) {
        Position position = PositionMapper.buildPosition(request);

        position.setIsActive(true);
        
        return PositionMapper.buildResponse(positionRepository.save(position));
    }

    @Transactional
    public void deactivatePosition(UUID positionId) {
        Position position = this.getById(positionId);
        position.setIsActive(false);
    }

    private Position getById(UUID positionId) {
        Position position = RepositoryUtils.findOrThrow(
            positionRepository, positionId, Position.class);

        return position;
    }

    private String normalizeSearchQuery(String query) {
        if (query == null) {
            return "";
        }
        return query.trim().toLowerCase().replaceAll("\\s{2,}", " ");
    }
}
