package org.ldv.chatbot.model.dao

import org.ldv.chatbot.model.entity.FournisseurIA
import org.springframework.data.jpa.repository.JpaRepository

interface FournisseurIADAO : JpaRepository<FournisseurIA, Long> {
    fun findByNomIgnoreCase(nom: String): FournisseurIA?
}