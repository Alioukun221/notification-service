package com.voom.notificationservice.event;

import com.voom.notificationservice.model.TypeNotification;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEvent {

    private String destinataireId;
    private TypeNotification type;
    private String titre;
    private String message;
    private String lienAction;
    private String sourceId;
}
