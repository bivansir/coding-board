package ru.coding_board.session_service.session;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.coding_board.session_service.candidate.Candidate;
import ru.coding_board.session_service.common.enums.SessionStatus;
import ru.coding_board.session_service.position.Position;


@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "sessions")
public class Session {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID sessionId;

    @Setter
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private SessionStatus sessionStatus;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @Setter
    @NotNull
    @Column(nullable = false)
    private UUID interviewerId;

    @Setter
    @NotNull
    @Column(nullable = false)
    private String inviteToken;

    @Setter
    @NotNull
    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    @Setter
    private LocalDateTime startedAt;

    @Setter
    private LocalDateTime completedAt;
}
