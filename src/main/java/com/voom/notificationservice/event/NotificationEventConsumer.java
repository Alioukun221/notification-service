package com.voom.notificationservice.event;

import com.voom.notificationservice.dto.CreateNotificationDTO;
import com.voom.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "voom.trajets.events",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consommerEvenementTrajet(NotificationEvent event) {
        log.info("Événement trajet reçu -> type={}", event.getType());
        traiterEvenement(event);
    }

    @KafkaListener(topics = "voom.affectations.events",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consommerEvenementAffectation(NotificationEvent event) {
        log.info("Événement affectation reçu -> type={}", event.getType());
        traiterEvenement(event);
    }

    @KafkaListener(topics = "voom.invitations.events",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consommerEvenementInvitation(NotificationEvent event) {
        log.info("Événement invitation reçu -> type={}", event.getType());
        traiterEvenement(event);
    }

    @KafkaListener(topics = "voom.amis.events",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consommerEvenementAmitie(NotificationEvent event) {
        log.info("Événement amitié reçu -> type={}", event.getType());
        traiterEvenement(event);
    }

    @KafkaListener(topics = "voom.avis.events",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consommerEvenementAvis(NotificationEvent event) {
        log.info("Événement avis reçu -> type={}", event.getType());
        traiterEvenement(event);
    }

    @KafkaListener(topics = "voom.documents.events",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consommerEvenementDocument(NotificationEvent event) {
        log.info("Événement document reçu -> type={}", event.getType());
        traiterEvenement(event);
    }

    @KafkaListener(topics = "voom.paiements.events",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consommerEvenementPaiement(NotificationEvent event) {
        log.info("Événement paiement reçu -> type={}", event.getType());
        traiterEvenement(event);
    }

    // Méthode commune

    private void traiterEvenement(NotificationEvent event) {
        try {
            CreateNotificationDTO dto = CreateNotificationDTO.builder()
                    .destinataireId(event.getDestinataireId())
                    .type(event.getType())
                    .titre(event.getTitre())
                    .message(event.getMessage())
                    .lienAction(event.getLienAction())
                    .sourceId(event.getSourceId())
                    .build();

            notificationService.creerNotificationAsync(dto);

        } catch (Exception e) {
            log.error("Erreur traitement événement [type={}] : {}",
                    event.getType(), e.getMessage(), e);
        }
    }
}
