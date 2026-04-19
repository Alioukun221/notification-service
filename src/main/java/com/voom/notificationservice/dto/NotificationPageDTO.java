package com.voom.notificationservice.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPageDTO {

    private List<NotificationResponseDTO> notifications;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private boolean dernierePage;
}
