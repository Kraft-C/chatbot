package org.ldv.chatbot.model.entity

import jakarta.persistence.*

/**
 * Représente une entreprise ou une plateforme qui propose des modèles IA.
 * Relation 1:* avec ModeleIA.
 */
@Entity
@Table(name = "fournisseur_ia")
class FournisseurIA(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var nom: String,

    /**
     * Un fournisseur propose un ou plusieurs modèles IA.
     * mappedBy = "fournisseurIA" fait référence à la propriété dans la classe ModeleIA.
     */
    @OneToMany(mappedBy = "fournisseurIA", fetch = FetchType.LAZY)
    var modelesIA: MutableList<ModeleIA> = mutableListOf()
) {
    // Méthode du diagramme

    fun envoyerRequete() {
        // TODO : Logique d'envoi de requête au fournisseur (probablement pour interagir avec son API)
        println("Envoi de requête au fournisseur $nom...")
    }
}