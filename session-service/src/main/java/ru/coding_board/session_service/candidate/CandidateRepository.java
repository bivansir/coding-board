package ru.coding_board.session_service.candidate;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.coding_board.session_service.candidate.dto.CandidateListItemResponse;

public interface CandidateRepository extends JpaRepository<Candidate, UUID> {
    @Query(
        value = """
            SELECT candidate_id, name, surname, patronymic, email
            FROM candidates
            WHERE (full_name_normalized ILIKE :fullName || '%'
                OR full_name_normalized % :fullName)
                AND is_active = :isActive
            ORDER BY similarity(full_name_normalized, :fullName) DESC
            """,
        countQuery = """
            SELECT count(*)
            FROM candidates
            WHERE (full_name_normalized ILIKE :fullName || '%' 
                OR full_name_normalized % :fullName)
                AND is_active = :isActive
                """,
         nativeQuery = true
    )
    Page<CandidateListItemResponse> searchByFullNameAndIsActive(
        @Param("fullName") String fullName,
        @Param("isActive") boolean isActive,
        Pageable pageable
    );

    @Query("""
        SELECT new ru.coding_board.session_service.candidate.dto.CandidateListItemResponse(
            c.candidateId, c.name, c.surname, c.patronymic, c.email)
        FROM Candidate c
        WHERE c.isActive = :isActive
        """)
    Page<CandidateListItemResponse> searchByIsActive(
        @Param("isActive") boolean isActive,
        Pageable pageable
    );
}
