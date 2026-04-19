package com.voom.notificationservice.service;

import com.voom.notificationservice.dto.*;
import com.voom.notificationservice.exception.NotificationNotFoundException;
import com.voom.notificationservice.model.Notification;
import com.voom.notificationservice.model.TypeNotification;
import com.voom.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    //  Création

    public NotificationResponseDTO creerNotification(CreateNotificationDTO dto) {
        log.info("Création notification [type={}] pour [id={}]",
                dto.getType(), dto.getDestinataireId());

        Notification notification = Notification.builder()
                .destinataireId(dto.getDestinataireId())
                .type(dto.getType())
                .titre(dto.getTitre())
                .message(dto.getMessage())
                .lienAction(dto.getLienAction())
                .sourceId(dto.getSourceId())
                .lue(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        log.debug("Notification créée [id={}]", saved.getId());

        return toResponseDTO(saved);
    }

    @Async
    public void creerNotificationAsync(CreateNotificationDTO dto) {
        try {
            creerNotification(dto);
        } catch (Exception e) {
            log.error("Erreur création async : {}", e.getMessage(), e);
        }
    }

    //  Consultation

    @Transactional(readOnly = true)
    public NotificationPageDTO obtenirNotificationsUtilisateur(
            String utilisateurId, int page, int taille) {

        Pageable pageable = PageRequest.of(page, taille);
        Page<Notification> resultPage = notificationRepository
                .findByDestinataireIdOrderByDateCreationDesc(utilisateurId, pageable);

        return buildPageDTO(resultPage);
    }

    @Transactional(readOnly = true)
    public NotificationPageDTO obtenirNotificationsNonLues(
            String utilisateurId, int page, int taille) {

        Pageable pageable = PageRequest.of(page, taille);
        Page<Notification> resultPage = notificationRepository
                .findByDestinataireIdAndLueFalseOrderByDateCreationDesc(utilisateurId, pageable);

        return buildPageDTO(resultPage);
    }

    @Transactional(readOnly = true)
    public NotificationPageDTO obtenirNotificationsParType(
            String utilisateurId, TypeNotification type, int page, int taille) {

        Pageable pageable = PageRequest.of(page, taille);
        Page<Notification> resultPage = notificationRepository
                .findByDestinataireIdAndTypeOrderByDateCreationDesc(utilisateurId, type, pageable);

        return buildPageDTO(resultPage);
    }

    @Transactional(readOnly = true)
    public NotificationResponseDTO obtenirNotificationParId(String id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(
                        "Notification introuvable : " + id));
        return toResponseDTO(notification);
    }

    //  Marquage

    public NotificationResponseDTO marquerCommeLue(String id, String utilisateurId) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(
                        "Notification introuvable : " + id));

        if (!notification.getDestinataireId().equals(utilisateurId)) {
            throw new NotificationNotFoundException(
                    "Cette notification n'appartient pas à l'utilisateur : " + utilisateurId);
        }

        if (!notification.getLue()) {
            notification.setLue(true);
            notification.setDateLecture(LocalDateTime.now());
            notificationRepository.save(notification);
        }

        return toResponseDTO(notification);
    }

    public void marquerToutesCommeLues(String utilisateurId) {
        log.info("Marquage toutes notifs comme lues pour [id={}]", utilisateurId);
        notificationRepository.marquerToutesCommeLues(utilisateurId);
    }

    // Statistiques

    @Transactional(readOnly = true)
    public NotificationStatsDTO obtenirStatistiques(String utilisateurId) {
        long total = notificationRepository.countByDestinataireId(utilisateurId);
        long nonLues = notificationRepository.countByDestinataireIdAndLueFalse(utilisateurId);

        return NotificationStatsDTO.builder()
                .utilisateurId(utilisateurId)
                .totalNotifications(total)
                .notificationsNonLues(nonLues)
                .notificationsLues(total - nonLues)
                .build();
    }

    // Suppression

    public void supprimerNotificationsLues(String utilisateurId) {
        log.info("Suppression notifs lues pour [id={}]", utilisateurId);
        notificationRepository.supprimerNotificationsLues(utilisateurId);
    }

    //  Helpers

    private NotificationResponseDTO toResponseDTO(Notification n) {
        return NotificationResponseDTO.builder()
                .id(n.getId())
                .destinataireId(n.getDestinataireId())
                .type(n.getType())
                .titre(n.getTitre())
                .message(n.getMessage())
                .dateCreation(n.getDateCreation())
                .lue(n.getLue())
                .lienAction(n.getLienAction())
                .sourceId(n.getSourceId())
                .dateLecture(n.getDateLecture())
                .build();
    }

    private NotificationPageDTO buildPageDTO(Page<Notification> page) {
        List<NotificationResponseDTO> notifications = page.getContent()
                .stream()
                .map(this::toResponseDTO)
                .toList();

        return NotificationPageDTO.builder()
                .notifications(notifications)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .dernierePage(page.isLast())
                .build();
    }
}