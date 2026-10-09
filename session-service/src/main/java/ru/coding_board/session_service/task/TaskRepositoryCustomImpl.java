package ru.coding_board.session_service.task;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import ru.coding_board.session_service.common.enums.Category;
import ru.coding_board.session_service.common.enums.Difficulty;
import ru.coding_board.session_service.common.enums.ProgrammingLanguage;
import ru.coding_board.session_service.task.dto.TaskListItemResponse;

@Repository 
@RequiredArgsConstructor 
public class TaskRepositoryCustomImpl implements TaskRepositoryCustom {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static final RowMapper<TaskListItemResponse> ROW_MAPPER = (rs, rowNum) -> {
    String programmingLanguage = rs.getString("programming_language");
    return new TaskListItemResponse(
        UUID.fromString(rs.getString("task_id")),
        rs.getString("title"),
        Difficulty.valueOf(rs.getString("difficulty")),
        Category.valueOf(rs.getString("category")),
        programmingLanguage != null ? ProgrammingLanguage.valueOf(programmingLanguage) : null
    );
};

    @Override 
    public Page<TaskListItemResponse> findByFilter(
        String searchQuery,
        Category category, 
        List<Difficulty> difficulties,
        ProgrammingLanguage programmingLanguage,
        List<UUID> filteredTaskIds,
        Pageable pageable) {

        boolean isSearch = searchQuery != null;
        MapSqlParameterSource params = new MapSqlParameterSource();

        String where = " WHERE 1=1";
        if (isSearch) {
            where += " AND title_tsv @@ query.tsq";
            params.addValue("searchQuery", searchQuery);
        }
        if (category != null) {
            where += " AND category = :category";
            params.addValue("category", category.name());
        }
        if (difficulties != null && !difficulties.isEmpty()) { 
            where += " AND difficulty IN (:difficulties)";
            params.addValue("difficulties", difficulties.stream()
                .map(d -> d.name()).toList());
        }
        if (programmingLanguage != null) {
            where += " AND programming_language = :programmingLanguage";
            params.addValue("programmingLanguage", programmingLanguage.name());
        }
        if (filteredTaskIds != null && !filteredTaskIds.isEmpty()) {
            where += " AND task_id NOT IN (:filteredTaskIds)";
            params.addValue("filteredTaskIds", filteredTaskIds);
        }

        String select = "SELECT task_id, title, difficulty, category, programming_language" 
            + (isSearch ? ", ts_rank(title_tsv, query.tsq) AS search_rank" : "");

        String from = " FROM tasks" 
            + (isSearch ? ", LATERAL (SELECT websearch_to_tsquery('russian', :searchQuery) "
            + "|| websearch_to_tsquery('english', :searchQuery) AS tsq) query" : "");

        String order = " ORDER BY" + (isSearch ? " search_rank DESC" : " task_id");
        
        String limit = " LIMIT :limit OFFSET :offset";
        

        String querySql = select + from + where + order + limit;
        String countSql = "SELECT count(*)" + from + where;

        params.addValue("limit", pageable.getPageSize());
        params.addValue("offset", pageable.getOffset());

        List<TaskListItemResponse> content = jdbcTemplate.query(querySql, params, ROW_MAPPER);
        Long total = jdbcTemplate.queryForObject(countSql, params, Long.class);

        return new PageImpl<>(content, pageable, total);
    }   
}
