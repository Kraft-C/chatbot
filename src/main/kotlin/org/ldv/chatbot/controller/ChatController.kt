package org.ldv.chatbot.controller

import org.ldv.chatbot.model.dao.ConversationDAO
import org.ldv.chatbot.model.dao.MessageDAO
import org.ldv.chatbot.model.dao.ModeleIADAO
import org.ldv.chatbot.model.dao.UtilisateurDAO
import org.ldv.chatbot.model.entity.Conversation
import org.ldv.chatbot.model.entity.Message
import org.ldv.chatbot.model.entity.TypeMessage
import org.ldv.chatbot.model.entity.Utilisateur
import org.ldv.chatbot.service.IAServiceFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@Controller
class ChatController(
    private val iaServiceFactory: IAServiceFactory,
    private val conversationDAO: ConversationDAO,
    private val messageDAO: MessageDAO,
    private val modeleIADAO: ModeleIADAO,
    private val utilisateurDAO: UtilisateurDAO
) {

    // Page d'accueil : liste des conversations
    @GetMapping("/", "/chat")
    fun accueil(model: Model, authentication: Authentication): String {
        //Recupere l'utilisateur de la session
        val utilisateur = this.getUtilisateurConnecte(authentication)
        // Ajout de l'utilisateur à la vue
        model.addAttribute("utilisateur", utilisateur)

        // Retire la conversation courante
        model.addAttribute("conversation", null)
        model.addAttribute("modeles", modeleIADAO.findAll())
        model.addAttribute("messages", emptyList<Message>())
        model.addAttribute(
            "conversations",
            conversationDAO.findAllByUtilisateurOrderByDateDerniereModificationDesc(utilisateur)
        )

        return "utilisateur/chat"
    }

    // Création d'une nouvelle conversation
    @PostMapping("/conversations")
    fun creerConversation(
        @RequestParam prompt: String,
        @RequestParam idModele: Long,
        authentication: Authentication
    ): String {
        // Recupere l'utilisateur
        val utilisateur = this.getUtilisateurConnecte(authentication)

        // Récupération du modèle sélectionné
        val modele = modeleIADAO.findById(idModele)
            .orElseThrow {
                ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Modèle IA introuvable"
                )
            }

        // Création de la conversation
        val conversation = Conversation(
            titre = prompt.take(50),
            dateCreation = LocalDateTime.now(),
            modeleIA = modele,
            utilisateur = utilisateur
        )

        val conversationEnregistree =
            conversationDAO.save(conversation)

        // Enregistrement du premier message
        val messageUtilisateur = Message(
            contenu = prompt,
            dateCreation = LocalDateTime.now(),
            typeMessage = TypeMessage.UTILISATEUR,
            conversation = conversationEnregistree
        )

        messageDAO.save(messageUtilisateur)

        try {
            // Sélection automatique du service
            val iaService = iaServiceFactory.getService(modele)

            // Appel du fournisseur correspondant
            val reponse = iaService.envoyerPrompt(prompt, modele)

            val messageAssistant = Message(
                contenu = reponse,
                dateCreation = LocalDateTime.now(),
                typeMessage = TypeMessage.ASSISTANT,
                conversation = conversationEnregistree
            )

            messageDAO.save(messageAssistant)

        } catch (e: Exception) {
            println("Erreur lors de l’appel au modèle ${modele.nom} : ${e.message}")
        }

        conversationEnregistree.dateDerniereModification =
            LocalDateTime.now()

        conversationDAO.save(conversationEnregistree)

        return "redirect:/conversations/${conversationEnregistree.id}"
    }

    // Affichage d'une conversation
    @GetMapping("/conversations/{id}")
    fun afficherConversation(
        @PathVariable id: Long,
        model: Model,
        authentication: Authentication
    ): String {
        //Recupere l'utilisateur
        val utilisateur = this.getUtilisateurConnecte(authentication)
        model.addAttribute("utilisateur", utilisateur)

        val conversation = conversationDAO
            .findByIdAndUtilisateur(id, utilisateur)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation introuvable")

        model.addAttribute(
            "conversations",
            conversationDAO.findAllByUtilisateurOrderByDateDerniereModificationDesc(
                utilisateur
            )
        )

        model.addAttribute("conversation", conversation)
        model.addAttribute(
            "messages",
            conversation.messages.sortedBy { it.dateCreation }
        )

        return "utilisateur/chat"
    }

    // Envoi d'un nouveau message dans une conversation existante
    @PostMapping("/conversations/{id}/messages")
    fun envoyerMessage(
        @PathVariable id: Long,
        @RequestParam prompt: String,
        authentication: Authentication
    ): String {
        val utilisateur = this.getUtilisateurConnecte(authentication)

        val conversation = conversationDAO
            .findByIdAndUtilisateur(id, utilisateur)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation introuvable")

        // Enregistrement du message utilisateur
        val messageUtilisateur = Message(
            contenu = prompt,
            dateCreation = LocalDateTime.now(),
            typeMessage = TypeMessage.UTILISATEUR,
            conversation = conversation
        )

        messageDAO.save(messageUtilisateur)

        try {
            val modele = conversation.modeleIA!!
            // Sélection automatique du service
            val iaService = iaServiceFactory.getService(modele)

            // Appel du fournisseur correspondant
            val reponse = iaService.envoyerPrompt(prompt, modele)

            val messageAssistant = Message(
                contenu = reponse,
                dateCreation = LocalDateTime.now(),
                typeMessage = TypeMessage.ASSISTANT,
                conversation = conversation
            )

            messageDAO.save(messageAssistant)

        } catch (e: Exception) {
            println("Erreur lors de l'appel à ${conversation.modeleIA!!.nom} : ${e.message}")
        }

        conversation.dateDerniereModification = LocalDateTime.now()
        conversationDAO.save(conversation)

        return "redirect:/conversations/$id"
    }

    // Suppression d'une conversation (ses messages sont supprimés en cascade)
    @PostMapping("/conversations/{id}/supprimer")
    fun supprimerConversation(@PathVariable id: Long): String {

        if (!conversationDAO.existsById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }

        conversationDAO.deleteById(id)

        return "redirect:/"
    }

    fun getUtilisateurConnecte(authentication: Authentication): Utilisateur {
        val email = authentication.name
        val utilisateur = utilisateurDAO
            .findByEmailIgnoreCase(email)
            ?: throw UsernameNotFoundException("Utilisateur introuvable")
        return utilisateur
    }
}

