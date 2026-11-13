package org.ldv.chatbot.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Représente une conversation entre un utilisateur et un modèle IA.
 * Relation N:1 avec Utilisateur.
 * Relation de composition 1:N avec Message.
 */
@Entity
@Table(name = "conversation")
class Conversation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var titre: String,

    @Column(nullable = false)
    var dateCreation: LocalDateTime,

    /**
     * Relation Many-to-One : Plusieurs conversations pour un seul utilisateur.
     * Côté propriétaire de la relation.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    var utilisateur: Utilisateur,

    /**
     * Relation One-to-Many : Une conversation contient plusieurs messages.
     * C'est une composition, donc CascadeType.ALL est approprié pour que les messages soient supprimés avec la conversation.
     * orphanRemoval = true assure que si un message est retiré de la liste, il est supprimé de la base de données.
     * mappedBy = "conversation" fait référence à la propriété dans la classe Message.
     */
    @OneToMany(mappedBy = "conversation", cascade = [CascadeType.ALL], orphanRemoval = true)
    val messages: MutableList<Message> = mutableListOf(),

    /**
     * Une conversation utilise un et un seul modèle IA à la fois.
     * (Basé sur la flèche dans le diagramme qui part de Conversation vers ModeleIA).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modele_ia_id") // Peut être null initialement si la conversation n'a pas encore sélectionné de modèle.
    var modeleIA: ModeleIA? = null

) {
    // Méthodes du diagramme

    fun ajouterMessage(contenu: String, role: RoleMessage): Message {
        // TODO : Créer un nouveau Message, l'associer à cette conversation (this), l'ajouter à la liste et le retourner.
        val nouveauMessage =
            Message(contenu = contenu, dateEnvoi = LocalDateTime.now(), role = role, conversation = this)
        messages.add(nouveauMessage)
        return nouveauMessage
    }

    fun consulterMessages(): List<Message> {
        // TODO : Retourner la liste des messages triés par dateEnvoi
        return this.messages.sortedBy { it.dateEnvoi }
    }
}