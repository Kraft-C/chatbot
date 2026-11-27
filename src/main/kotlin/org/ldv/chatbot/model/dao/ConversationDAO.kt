package org.ldv.chatbot.model.dao

import org.ldv.chatbot.model.entity.Conversation
import org.ldv.chatbot.model.entity.ModeleIA
import org.ldv.chatbot.model.entity.Utilisateur
import org.springframework.data.jpa.repository.JpaRepository

interface ConversationDAO : JpaRepository<Conversation, Long> {
    val findAllByOrderByDateDerniereModificationDesc: Any?

    fun findAllByUtilisateurOrderByDateDerniereModificationDesc(utilisateur: Utilisateur): List<Conversation>
    fun findByIdAndUtilisateur(id:Long,utilisateur: Utilisateur):Conversation?
    fun existsByModeleIA(modeleIA: ModeleIA): Boolean
}
