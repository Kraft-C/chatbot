package org.ldv.chatbot.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class ChatBotController {

    @GetMapping("/")
    fun accueil(): String = "redirect:/connexion"

    @GetMapping("/connexion")
    fun afficherConnexion(model: Model): String {
        model.addAttribute("showHeader", false)
        model.addAttribute("pageTitle", "PAWLY - Connexion")
        return "auth/connexion"
    }

    // TEMPORAIRE : cette route affiche le formulaire et confirme sa réception.
    // Elle n'authentifie PAS encore l'utilisateur. Spring Security sera ajouté ensuite.
    @PostMapping("/connexion")
    fun traiterConnexion(
        @RequestParam email: String,
        @RequestParam password: String,
        redirectAttributes: RedirectAttributes
    ): String {
        if (email.isBlank() || password.isBlank()) {
            redirectAttributes.addFlashAttribute("erreur", "Veuillez renseigner votre adresse e-mail et votre mot de passe.")
            return "redirect:/connexion"
        }

        redirectAttributes.addFlashAttribute(
            "information",
            "Le formulaire fonctionne. L'authentification sera activée lors de la configuration de Spring Security."
        )
        return "redirect:/connexion"
    }

    @GetMapping("/chat")
    fun afficherChat(model: Model): String {
        model.addAttribute("showHeader", false)
        model.addAttribute("pageTitle", "PAWLY - Nouveau chat")
        model.addAttribute("activePage", "chat")
        return "utilisateur/chat"
    }
}