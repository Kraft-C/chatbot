package org.ldv.chatbot.dto.gemini

data class GeminiChatResponse(
    val candidates: List<GeminiCandidate>
)