package org.ldv.chatbot.dto.mistral

data class MistralChatRequest(
    val model: String,
    val messages: List<MistralMessage>
)
