package com.voom.notificationservice.model;

public enum TypeNotification {

    // Trajets
    AFFECTATION_PROPOSEE,
    AFFECTATION_ACCEPTEE,
    AFFECTATION_REFUSEE,
    TRAJET_DEMARRE,
    TRAJET_TERMINE,
    TRAJET_ANNULE,

    // Demandes
    DEMANDE_PRISE_EN_CHARGE,
    DEMANDE_ANNULEE,

    // Invitations
    INVITATION_TRAJET_RECUE,
    INVITATION_ACCEPTEE,
    INVITATION_REFUSEE,

    // Amis
    DEMANDE_AMITIE_RECUE,
    DEMANDE_AMITIE_ACCEPTEE,

    // Avis
    AVIS_RECU,

    // Signalements
    SIGNALEMENT_TRAITE,

    // Documents
    DOCUMENT_VERIFIE,
    DOCUMENT_REJETE,

    // Tokens
    ACHAT_TOKEN_CONFIRME,
    SOLDE_TOKEN_FAIBLE,
    CREDITS_DEBITES,

    // Système
    SYSTEME
}