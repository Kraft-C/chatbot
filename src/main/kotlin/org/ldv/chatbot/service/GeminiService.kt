package org.ldv.chatbot.service

import org.ldv.chatbot.dto.gemini.GeminiChatRequest
import org.ldv.chatbot.dto.gemini.GeminiChatResponse
import org.ldv.chatbot.dto.gemini.GeminiContent
import org.ldv.chatbot.dto.gemini.GeminiPart
import org.ldv.chatbot.model.entity.ModeleIA
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class GeminiService(
    private val restClientBuilder: RestClient.Builder,

    @Value("\${gemini.api.key:}")
    private val apiKey: String

) : IAService {
    override fun getFournisseur(): String = "Gemini"

    override fun envoyerPrompt(prompt: String, modele: ModeleIA): String {

        // Étape 1 : Création du message de l'utilisateur
     val part = GeminiPart(text = prompt)


        // Étape 2 : Préparation de la requête pour Gemini
        val content = GeminiContent(
          role="user",
           parts = listOf(part)
        )

        val request = GeminiChatRequest(listOf(content))
        // Étape 3 : Envoi de la requête HTTP POST à l'API
        val client = restClientBuilder.build()

        val response = client.post()
            .uri(modele.urlAPI)
            .header("x-goog-api-key", apiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(GeminiChatResponse::class.java)

        // Étape 4 : Récupération du texte de la réponse
        val candidat = response?.candidates?.firstOrNull()
        val reponse = candidat?.content?.parts?.firstOrNull()?.text

        if (reponse != null) {
            return reponse
        } else {
            return "Aucune réponse reçue de Gemini."
        }
    }

}