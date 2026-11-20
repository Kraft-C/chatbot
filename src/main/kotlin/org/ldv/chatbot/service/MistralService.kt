package org.ldv.chatbot.service

import org.ldv.chatbot.dto.mistral.MistralChatRequest
import org.ldv.chatbot.dto.mistral.MistralChatResponse
import org.ldv.chatbot.dto.mistral.MistralMessage
import org.ldv.chatbot.model.entity.ModeleIA
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class MistralService(
    private val restClientBuilder: RestClient.Builder,

    @Value("\${mistral.api.key}")
    private val apiKey: String

) : IAService {
    override fun getFournisseur(): String = "Mistral"

    override fun envoyerPrompt(prompt: String, modele: ModeleIA): String {

        // Étape 1 : Création du message de l'utilisateur
        val message = MistralMessage(
            role = "user",
            content = prompt
        )

        // Étape 2 : Préparation de la requête pour Mistral
        val request = MistralChatRequest(
            model = modele.nom,
            messages = listOf(message)
        )

        // Étape 3 : Envoi de la requête HTTP POST à l'API
        val client = restClientBuilder.build()

        val response = client.post()
            .uri(modele.urlAPI)
            .header("Authorization", "Bearer $apiKey")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(MistralChatResponse::class.java)

        // Étape 4 : Récupération du texte de la réponse
        val choix = response?.choices?.firstOrNull()
        val reponse = choix?.message?.content

        if (reponse != null) {
            return reponse
        } else {
            return "Aucune réponse reçue de Mistral."
        }
    }

}
