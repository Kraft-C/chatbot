package org.ldv.chatbot.model.dao

import org.ldv.chatbot.model.entity.Message
import org.ldv.chatbot.model.entity.TypeMessage
import org.springframework.data.jpa.repository.JpaRepository

interface MessageDAO : JpaRepository<Message, Long> {
    fun findAllByConversation_IdOrderByDateCreationAsc(conversationId: Long): List<Message>
    fun findAllByConversation_IdAndTypeMessageOrderByDateCreationAsc(
        conversationId: Long,
        typeMessage: TypeMessage
    ): List<Message>
}