package ru.coding_board.session_service.task;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.coding_board.session_service.common.enums.Category;
import ru.coding_board.session_service.common.enums.Difficulty;
import ru.coding_board.session_service.common.enums.ProgrammingLanguage;


@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "tasks")
public class Task {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID taskId;

    @Setter
    @NotNull
    @Column(nullable = false, length = 255)
    private String title;

    @Setter
    @NotNull
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Setter
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @Setter
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Setter
    @Enumerated(EnumType.STRING)
    private ProgrammingLanguage programmingLanguage;

    @Setter
    @NotNull
    @Column(nullable = false)
    private Boolean isActive;
}
