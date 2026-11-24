package org.ldv.chatbot.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class MainController {


    @GetMapping("/login")
    fun login(): String {
        return "auth/login"

    }


    // TEMPORAIRE : cette route affiche le formulaire et confirme sa réception.
    // Elle n'authentifie PAS encore l'utilisateur. Spring Security sera ajouté ensuite.



}