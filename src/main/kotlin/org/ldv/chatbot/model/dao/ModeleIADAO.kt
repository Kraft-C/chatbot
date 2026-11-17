package org.ldv.chatbot.model.dao

import org.ldv.chatbot.model.entity.ModeleIA
import org.springframework.data.jpa.repository.JpaRepository

interface ModeleIADAO : JpaRepository<ModeleIA, Long> {
    fun findAllByFournisseurIA_Id(fournisseurId: Long): List<ModeleIA>
    fun findAllByActifTrue(): List<ModeleIA>
}