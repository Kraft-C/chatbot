package org.ldv.chatbot.model.entity

import jakarta.persistence.*

/**
 * Représente un modèle d'intelligence artificielle spécifique (ex: GPT-3.5, Llama-2).
 * Relation N:1 avec FournisseurIA.
 */
@Entity
@Table(name = "modele_ia")
class ModeleIA(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var nom: String,

    @Column(nullable = false, unique = true) // Identifiant unique spécifique à l'API du fournisseur
    var identifiantAPI: String,

    @Column(nullable = false)
    var urlAPI: String,

    @Column(nullable = false)
    var actif: Boolean,

    /**
     * Relation Many-to-One : Plusieurs modèles pour un seul fournisseur.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fournisseur_ia_id", nullable = false)
    var fournisseurIA: FournisseurIA
) {
    // Méthode du diagramme

    fun genererReponse(messageUtilisateur: Message): String {
        // TODO : Logique de génération de réponse par le modèle
        // Cela nécessitera probablement d'appeler le service d'API du fournisseur
        return "Réponse générée par le modèle ${this.nom} pour le message : '${messageUtilisateur.contenu}'"
    }
}