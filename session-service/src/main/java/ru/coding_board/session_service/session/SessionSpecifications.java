package ru.coding_board.session_service.session;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import ru.coding_board.session_service.common.enums.SessionStatus;


public class SessionSpecifications {
    
    public static Specification<Session> scheduledAfter(LocalDate startDate) {
        return (root, query, cb) ->
            startDate == null ? null : cb.greaterThanOrEqualTo(root.get("scheduledAt"), startDate.atStartOfDay());
    }

    public static Specification<Session> scheduledBefore(LocalDate endDate) {
        return (root, query, cb) ->
            endDate == null ? null : cb.lessThan(root.get("scheduledAt"), endDate.plusDays(1).atStartOfDay());
    }

    public static Specification<Session> hasPositionIn(List<UUID> position_ids) {
        return (root, query, cb) ->
            position_ids == null ? null : root.get("position").get("positionId").in(position_ids);
    }

    public static Specification<Session> statusEquals(SessionStatus status) {
        return (root, query, cb) ->
            status == null ? null : root.get("status").equalTo(status);
    }

    public static Specification<Session> interviewerIdEquals(UUID interviewer_id) {
        return (root, query, cb) ->
            interviewer_id == null ? null : root.get("interviewerId").equalTo(interviewer_id);
    }

    public static Specification<Session> CandidateIdEquals(UUID candidate_id) {
        return (root, query, cb) ->
            candidate_id == null ? null : root.get("candidate").get("candidateId").equalTo(candidate_id);
    }
}
