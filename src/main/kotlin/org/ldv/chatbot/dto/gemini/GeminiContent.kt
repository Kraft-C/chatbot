package org.ldv.chatbot.dto.gemini

data class GeminiContent (
    val role: String,
    val parts: List<GeminiPart>
){

}