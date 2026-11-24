package org.ldv.chatbot.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Utilisateur de PAWLY, propriétaire de ses conversations.
 */
@Entity
@Table(name = "utilisateur")
class Utilisateur(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var nom: String,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var motDePasse: String?,

    @OneToMany(
        mappedBy = "utilisateur",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var conversations: MutableList<Conversation> = mutableListOf(),
    var prenom: String,
    @ManyToOne
    @JoinColumn("role_id")
    var role: Role
) {
    fun creerConversation(titre: String, modeleIA: ModeleIA? = null): Conversation {
        val conversation = Conversation(
            titre = titre,
            dateCreation = LocalDateTime.now(),
            utilisateur = this,
            modeleIA = modeleIA
        )
        conversations.add(conversation)
        modeleIA?.conversations?.add(conversation)
        return conversation
    }

    fun supprimerConversation(conversation: Conversation) {
        conversations.remove(conversation)
    }

    fun envoyerMessage(conversation: Conversation, contenu: String): Message {
        require(conversation.utilisateur === this || conversation.utilisateur?.id == id) {
            "Cette conversation n'appartient pas à cet utilisateur."
        }
        return conversation.ajouterMessage(contenu, TypeMessage.UTILISATEUR)
    }

    fun consulterConversations(): List<Conversation> =
        conversations.sortedByDescending { it.dateCreation }
}