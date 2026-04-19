package com.voom.notificationservice.repository;

import com.voom.notificationservice.model.Notification;
import com.voom.notificationservice.model.TypeNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    // Toutes les notifications d'un utilisateur
    Page<Notification> findByDestinataireIdOrderByDateCreationDesc(
            String destinataireId, Pageable pageable);

    // Uniquement les non lues
    Page<Notification> findByDestinataireIdAndLueFalseOrderByDateCreationDesc(
            String destinataireId, Pageable pageable);

    // Filtrer par type
    Page<Notification> findByDestinataireIdAndTypeOrderByDateCreationDesc(
            String destinataireId, TypeNotification type, Pageable pageable);

    // Compter toutes les notifications
    long countByDestinataireId(String destinataireId);

    // Compter les non lues
    long countByDestinataireIdAndLueFalse(String destinataireId);

    // Marquer toutes comme lues
    @Modifying
    @Query("UPDATE Notification n SET n.lue = true, n.dateLecture = CURRENT_TIMESTAMP " +
            "WHERE n.destinataireId = :destinataireId AND n.lue = false")
    void marquerToutesCommeLues(@Param("destinataireId") String destinataireId);

    // Supprimer les notifications lues
    @Modifying
    @Query("DELETE FROM Notification n " +
            "WHERE n.destinataireId = :destinataireId AND n.lue = true")
    void supprimerNotificationsLues(@Param("destinataireId") String destinataireId);
}