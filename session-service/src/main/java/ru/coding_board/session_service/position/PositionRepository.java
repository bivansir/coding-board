package ru.coding_board.session_service.position;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.coding_board.session_service.position.dto.PositionResponse;


@Repository
public interface PositionRepository extends JpaRepository<Position, UUID> {

    @Query(
        value = """
            SELECT position_id, title
            FROM positions
            WHERE (title ILIKE :title || '%' 
                OR title % :title)
                AND is_active
            ORDER BY similarity(title, :title) DESC
            LIMIT 5;
            """,
         nativeQuery = true
    )
    List<PositionResponse> searchByTitleAndIsActiveTrue (
        @Param("title") String title
    );
}
