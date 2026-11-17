package org.ldv.chatbot.model.entity

import jakarta.persistence.*

/**
 * Entreprise ou plateforme proposant des modèles d'intelligence artificielle.
 */
@Entity
@Table(name = "fournisseur_ia")
class FournisseurIA(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var nom: String,

    // Le fournisseur est le côté inverse : la clé étrangère est dans ModeleIA.
    @OneToMany(
        mappedBy = "fournisseurIA",
        cascade = [CascadeType.PERSIST, CascadeType.MERGE]
    )
    var modelesIA: MutableList<ModeleIA> = mutableListOf(),
    cheminLogo: String,
    description: String
) {
    fun ajouterModele(modele: ModeleIA) {
        modelesIA.add(modele)
        modele.fournisseurIA = this
    }

    fun retirerModele(modele: ModeleIA) {
        modelesIA.remove(modele)
    }
}