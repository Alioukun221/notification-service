package com.voom.notificationservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationStatsDTO {

    private String utilisateurId;
    private long totalNotifications;
    private long notificationsNonLues;
    private long notificationsLues;
}