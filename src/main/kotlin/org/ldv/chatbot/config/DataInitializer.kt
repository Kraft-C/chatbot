package org.ldv.chatbot.config

import org.ldv.chatbot.model.dao.FournisseurIADAO
import org.ldv.chatbot.model.dao.ModeleIADAO
import org.ldv.chatbot.model.entity.FournisseurIA
import org.ldv.chatbot.model.entity.ModeleIA
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataInitializer(
    private val fournisseurIADAO: FournisseurIADAO,
    private val modeleIADAO: ModeleIADAO
) {

    @Bean
    fun initData() = CommandLineRunner {

        // Récupérer ou créer le fournisseur Mistral
        val mistral = fournisseurIADAO.findAll()
            .firstOrNull { it.nom == "Mistral" }
            ?: fournisseurIADAO.save(
                FournisseurIA(
                    nom = "Mistral",
                    cheminLogo = "/images/mistral.png",
                    description = "Fournisseur français de modèles d'intelligence artificielle."
                )
            )

        // Récupérer ou créer le fournisseur Gemini
        val gemini = fournisseurIADAO.findAll()
            .firstOrNull { it.nom == "Gemini" }
            ?: fournisseurIADAO.save(
                FournisseurIA(
                    nom = "Gemini",
                    cheminLogo = "/images/gemini.png",
                    description = "Modèles d'intelligence artificielle de Google."
                )
            )

        // Définition des modèles IA
        val modeles = listOf(

            ModeleIA(
                nom = "gemini-3.8-flash",
                identifiantAPI = "gemini-3.8-flash",
                urlAPI = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash:generateContent",
                actif = true,
                fournisseurIA = gemini,
                description = "Modèle rapide et polyvalent.",
                fourniseurIA = gemini,
                cheminLogo = "/images/gemini.png"
            ),

            ModeleIA(
                nom = "gemini-3.5-flash-lite",
                identifiantAPI = "gemini-3.5-flash-lite",
                urlAPI = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash-lite:generateContent",
                actif = true,
                fournisseurIA = gemini,
                description = "Modèle rapide pour les tâches simples.",
                fourniseurIA = gemini,
                cheminLogo = "/images/gemini.png"
            ),

            ModeleIA(
                nom = "mistral-small-latest",
                identifiantAPI = "mistral-small-latest",
                urlAPI = "https://api.mistral.ai/v1/chat/completions",
                actif = true,
                fournisseurIA = mistral,
                description = "Modèle léger pour les conversations et les tâches courantes.",
                fourniseurIA = mistral,
                cheminLogo = "/images/mistral.png"
            ),

            ModeleIA(
                nom = "mistral-medium-latest",
                identifiantAPI = "mistral-medium-latest",
                urlAPI = "https://api.mistral.ai/v1/chat/completions",
                actif = true,
                fournisseurIA = mistral,
                description = "Modèle plus avancé pour les tâches complexes.",
                fourniseurIA = mistral,
                cheminLogo = "/images/mistral.png"
            )
        )

        // Récupérer les modèles déjà présents
        val modelesExistants = modeleIADAO.findAll()
            .map { it.nom }
            .toSet()

        // Enregistrer uniquement les nouveaux modèles
        modeles
            .filter { it.nom !in modelesExistants }
            .forEach { modeleIADAO.save(it) }
    }
}