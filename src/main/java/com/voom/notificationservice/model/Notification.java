package com.voom.notificationservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_destinataire_id", columnList = "destinataire_id"),
        @Index(name = "idx_date_creation", columnList = "date_creation"),
        @Index(name = "idx_lue", columnList = "lue")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "destinataire_id", nullable = false)
    private String destinataireId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeNotification type;

    @Column(nullable = false, length = 150)
    private String titre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(nullable = false)
    @Builder.Default
    private Boolean lue = false;

    @Column(name = "lien_action", length = 500)
    private String lienAction;

    @Column(name = "source_id")
    private String sourceId;

    @Column(name = "date_lecture")
    private LocalDateTime dateLecture;
}