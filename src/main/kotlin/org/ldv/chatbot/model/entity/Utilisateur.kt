package org.ldv.chatbot.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Représente un utilisateur de l'application.
 * Relation 1:* avec Conversation.
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
    var motDePasse: String,

    /**
     * Une conversation appartient à exactement un utilisateur.
     * Un utilisateur peut posséder plusieurs conversations.
     * Le fetch eager (ou lazy) dépendra de vos besoins de performance.
     * mappedBy = "utilisateur" fait référence à la propriété dans la classe Conversation.
     */
    @OneToMany(mappedBy = "utilisateur", fetch = FetchType.LAZY)
    var conversations: MutableList<Conversation> = mutableListOf()
) {
    // Méthodes du diagramme (à implémenter selon votre logique métier)

    fun seConnecter() {
        // TODO : Implémenter la logique de connexion
        println("Utilisateur $email en cours de connexion...")
    }

    fun creerConversation(titre: String): Conversation {
        // TODO : Créer et retourner une nouvelle instance de Conversation
        // N'oubliez pas d'associer cette conversation à l'utilisateur actuel (this)
        val nouvelleConv = Conversation(titre = titre, dateCreation = LocalDateTime.now(), utilisateur = this)
        conversations.add(nouvelleConv)
        return nouvelleConv
    }

    fun envoyerMessage(conversation: Conversation, contenu: String): Message {
        // TODO : Créer un message et l'ajouter à la conversation
        // L'utilisateur envoie un message, donc son rôle est UTILISATEUR.
        // Cela devrait probablement appeler conversation.ajouterMessage()
        return conversation.ajouterMessage(contenu, RoleMessage.UTILISATEUR)
    }

    fun consulterConversations(): List<Conversation> {
        // TODO : Retourner la liste des conversations de l'utilisateur
        return this.conversations
    }
}