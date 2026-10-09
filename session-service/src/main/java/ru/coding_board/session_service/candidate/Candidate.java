package ru.coding_board.session_service.candidate;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "candidates")
public class Candidate {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID candidateId;

    @NotNull 
    @Column(nullable = false, length = 255)
    private String name;

    @NotNull
    @Column(nullable = false, length = 255)
    private String surname;

    @Column(nullable = false, length = 255)
    private String patronymic;

    @NotNull
    @Column(nullable = false, length = 255)
    private String email;

    @Column(length = 50)
    private String phoneNumber;

    @Column(length = 255)
    private String resumeId;

    @NotNull
    @Column(nullable = false)
    private Boolean isActive;

    @Column(name = "full_name_normalized", insertable = false, updatable = false)
    private String fullNameNormalized;


}
