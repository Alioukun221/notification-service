package com.voom.notificationservice.dto;

import com.voom.notificationservice.model.TypeNotification;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDTO {

    private String id;
    private String destinataireId;
    private TypeNotification type;
    private String titre;
    private String message;
    private LocalDateTime dateCreation;
    private Boolean lue;
    private String lienAction;
    private String sourceId;
    private LocalDateTime dateLecture;
}