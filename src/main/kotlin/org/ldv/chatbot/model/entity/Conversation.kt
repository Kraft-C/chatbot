package org.ldv.chatbot.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Une conversation appartient à un utilisateur, utilise éventuellement un modèle IA
 * et contient plusieurs messages.
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
    var dateCreation: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var dateDerniereModification: LocalDateTime = LocalDateTime.now(),

    // Plusieurs conversations peuvent appartenir au même utilisateur.
    // Conversation est propriétaire de cette relation.
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "utilisateur_id", nullable = true)
    var utilisateur: Utilisateur? = null,

    // Une conversation est composée de ses messages.
    @OneToMany(mappedBy = "conversation", cascade = [CascadeType.ALL], orphanRemoval = true)
    var messages: MutableList<Message> = mutableListOf(),

    // Une conversation peut être associée à un modèle IA.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modele_ia_id")
    var modeleIA: ModeleIA? = null
) {
    fun ajouterMessage(contenu: String, typeMessage: TypeMessage): Message {
        val message = Message(
            contenu = contenu,
            dateCreation = LocalDateTime.now(),
            typeMessage = typeMessage,
            conversation = this
        )
        messages.add(message)
        return message
    }

    fun retirerMessage(message: Message) {
        messages.remove(message)
    }

    fun changerModeleIA(nouveauModele: ModeleIA?) {
        modeleIA?.conversations?.remove(this)
        modeleIA = nouveauModele
        if (nouveauModele != null && !nouveauModele.conversations.contains(this)) {
            nouveauModele.conversations.add(this)
        }
    }

    fun consulterMessages(): List<Message> =
        messages.sortedBy { it.dateCreation }
}