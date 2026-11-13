package org.ldv.chatbot.model.dao

import org.ldv.chatbot.model.entity.Message
import org.springframework.data.jpa.repository.JpaRepository

interface ConversationDAO : JpaRepository<Message, Long> {
}