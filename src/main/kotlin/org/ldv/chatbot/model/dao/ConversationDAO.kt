package org.ldv.chatbot.model.dao

import org.ldv.chatbot.model.entity.Conversation
import org.springframework.data.jpa.repository.JpaRepository

interface ConversationDAO : JpaRepository<Conversation, Long> {
    fun findAllByOrderByDateDerniereModificationDesc(): List<Conversation>
}
