package com.voom.notificationservice.controller;

import com.voom.notificationservice.dto.*;
import com.voom.notificationservice.model.TypeNotification;
import com.voom.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    // ─── Création ─────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<NotificationResponseDTO> creerNotification(
            @Valid @RequestBody CreateNotificationDTO dto) {

        log.info("POST /api/v1/notifications - type={}", dto.getType());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(notificationService.creerNotification(dto));
    }

    // ─── Consultation ─────────────────────────────────────────────────────

    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<NotificationPageDTO> obtenirNotifications(
            @PathVariable String utilisateurId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int taille) {

        return ResponseEntity.ok(
                notificationService.obtenirNotificationsUtilisateur(
                        utilisateurId, page, taille));
    }

    @GetMapping("/utilisateur/{utilisateurId}/non-lues")
    public ResponseEntity<NotificationPageDTO> obtenirNotificationsNonLues(
            @PathVariable String utilisateurId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int taille) {

        return ResponseEntity.ok(
                notificationService.obtenirNotificationsNonLues(
                        utilisateurId, page, taille));
    }

    @GetMapping("/utilisateur/{utilisateurId}/type/{type}")
    public ResponseEntity<NotificationPageDTO> obtenirNotificationsParType(
            @PathVariable String utilisateurId,
            @PathVariable TypeNotification type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int taille) {

        return ResponseEntity.ok(
                notificationService.obtenirNotificationsParType(
                        utilisateurId, type, page, taille));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> obtenirNotificationParId(
            @PathVariable String id) {

        return ResponseEntity.ok(
                notificationService.obtenirNotificationParId(id));
    }

    // ─── Marquage ─────────────────────────────────────────────────────────

    @PatchMapping("/{id}/lue")
    public ResponseEntity<NotificationResponseDTO> marquerCommeLue(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String utilisateurId) {

        return ResponseEntity.ok(
                notificationService.marquerCommeLue(id, utilisateurId));
    }

    @PatchMapping("/utilisateur/{utilisateurId}/tout-lire")
    public ResponseEntity<Void> marquerToutesCommeLues(
            @PathVariable String utilisateurId) {

        notificationService.marquerToutesCommeLues(utilisateurId);
        return ResponseEntity.noContent().build();
    }

    // ─── Statistiques ─────────────────────────────────────────────────────

    @GetMapping("/utilisateur/{utilisateurId}/stats")
    public ResponseEntity<NotificationStatsDTO> obtenirStatistiques(
            @PathVariable String utilisateurId) {

        return ResponseEntity.ok(
                notificationService.obtenirStatistiques(utilisateurId));
    }

    // ─── Suppression ──────────────────────────────────────────────────────

    @DeleteMapping("/utilisateur/{utilisateurId}/lues")
    public ResponseEntity<Void> supprimerNotificationsLues(
            @PathVariable String utilisateurId) {

        notificationService.supprimerNotificationsLues(utilisateurId);
        return ResponseEntity.noContent().build();
    }
}