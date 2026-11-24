package org.ldv.chatbot.model.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany


@Entity
class Role (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var id: Long? = null,
    // Nom affiché dans les templates
    var nom: String,
    // Nom utilisé par Spring Security
    var nomLogique: String,
    @OneToMany(
        mappedBy = "role",
        cascade = [CascadeType.REMOVE],
        orphanRemoval = true
    )
    var utilisateurs: MutableList<Utilisateur> = mutableListOf()
){
}
