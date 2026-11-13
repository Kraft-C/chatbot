package org.ldv.chatbot.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Représente un message individuel dans une conversation.
 * Relation N:1 avec Conversation.
 * Utilise l'énumération RoleMessage.
 */
@Entity
@Table(name = "message")
class Message(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, columnDefinition = "TEXT") // TEXT pour supporter les longs textes
    var contenu: String,

    @Column(nullable = false)
    var dateEnvoi: LocalDateTime,

    /**
     * Stocke le rôle du message (UTILISATEUR ou ASSISTANT) sous forme de chaîne de caractères en base.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: RoleMessage,

    /**
     * Relation Many-to-One : Plusieurs messages pour une seule conversation.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    var conversation: Conversation
)


