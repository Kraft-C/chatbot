package org.ldv.chatbot.model.entity

import jakarta.persistence.*

/**
 * Modèle d'intelligence artificielle proposé par un fournisseur.
 */
@Entity
@Table(name = "modele_ia")
class ModeleIA(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var nom: String,

    @Column(nullable = false, unique = true)
    var identifiantAPI: String,

    @Column(nullable = false)
    var urlAPI: String,

    @Column(nullable = false)
    var actif: Boolean = true,

    // Plusieurs modèles peuvent appartenir à un même fournisseur.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fournisseur_ia_id", nullable = false)
    var fournisseurIA: FournisseurIA,

    // Un même modèle IA peut être utilisé dans plusieurs conversations.
    @OneToMany(mappedBy = "modeleIA", fetch = FetchType.LAZY)
    var conversations: MutableList<Conversation> = mutableListOf(),
    description: String,

    cheminLogo: String
)