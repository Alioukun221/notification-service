package com.voom.notificationservice.dto;

import com.voom.notificationservice.model.TypeNotification;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateNotificationDTO {

    @NotBlank(message = "L'identifiant du destinataire est obligatoire")
    private String destinataireId;

    @NotNull(message = "Le type de notification est obligatoire")
    private TypeNotification type;

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    @NotBlank(message = "Le message est obligatoire")
    private String message;

    private String lienAction;

    private String sourceId;
}
