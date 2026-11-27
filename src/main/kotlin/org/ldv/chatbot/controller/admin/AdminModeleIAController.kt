package org.ldv.chatbot.controller.admin

import org.ldv.chatbot.model.dao.ConversationDAO
import org.ldv.chatbot.model.dao.FournisseurIADAO
import org.ldv.chatbot.model.dao.ModeleIADAO
import org.ldv.chatbot.model.dto.ModeleIAForm
import org.ldv.chatbot.model.entity.ModeleIA
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/admin/modeles-ia")
class AdminModeleIAController(
    private val modeleIADAO: ModeleIADAO,
    private val fourniseurIADAO: FournisseurIADAO,
    private val conversationDAO: ConversationDAO
) {

    @ModelAttribute("showHeader")
    fun showHeader(): Boolean = true

    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("modelesIA", modeleIADAO.findAll())
        return "admin/modeleIA/index"
    }

    @GetMapping("/create")
    fun create(model: Model): String {
        model.addAttribute("fourniseursIA", fourniseurIADAO.findAll())
        return "admin/modeleIA/create"
    }

    @PostMapping
    fun store(
        @ModelAttribute formulaire: ModeleIAForm,
        redirectAttributes: RedirectAttributes
    ): String {
        if (formulaire.nom.isBlank() || formulaire.urlAPI.isBlank()) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Le nom et l'URL sont obligatoires"
            )
        }
        val fourniseurIAId = formulaire.fourniseurIAId
            ?: throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Fournisseur IA obligatoire"
            )
        val fourniseurIA = fourniseurIADAO.findById(fourniseurIAId)
            .orElseThrow {
                ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Fournisseur IA introuvable"
                )
            }
        val modeleIA = ModeleIA(
            nom = formulaire.nom,
            urlAPI = formulaire.urlAPI,
            description = formulaire.description,
            cheminLogo = formulaire.cheminLogo,
            fournisseurIA = fourniseurIA
        )
        modeleIADAO.save(modeleIA)
        redirectAttributes.addFlashAttribute(
            "succes", "Le modèle IA a été créé avec succès."
        )
        return "redirect:/admin/modeles-ia"
    }

    @GetMapping("/{id}")
    fun show(@PathVariable id: Long, model: Model): String {
        val modeleIA = modeleIADAO.findById(id).orElseThrow {
            ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Modèle IA introuvable"
            )
        }
        model.addAttribute("modeleIA", modeleIA)
        return "admin/modeleIA/show"
    }

    @GetMapping("/{id}/edit")
    fun edit(@PathVariable id: Long, model: Model): String {
        val modeleIA = modeleIADAO.findById(id).orElseThrow {
            ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Modèle IA introuvable"
            )
        }
        model.addAttribute("modeleIA", modeleIA)
        model.addAttribute("fourniseursIA", fourniseurIADAO.findAll())
        return "admin/modeleIA/edit"
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @ModelAttribute formulaire: ModeleIAForm,
        redirectAttributes: RedirectAttributes
    ): String {
        if (formulaire.nom.isBlank() || formulaire.urlAPI.isBlank()) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Le nom et l'URL sont obligatoires"
            )
        }
        val modeleIA = modeleIADAO.findById(id).orElseThrow {
            ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Modèle IA introuvable"
            )
        }
        val fourniseurIAId = formulaire.fourniseurIAId
            ?: throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Fournisseur IA obligatoire"
            )
        val fourniseurIA = fourniseurIADAO.findById(fourniseurIAId)
            .orElseThrow {
                ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Fournisseur IA introuvable"
                )
            }
        modeleIA.nom = formulaire.nom
        modeleIA.urlAPI = formulaire.urlAPI
        modeleIA.description = formulaire.description
        modeleIA.cheminLogo = formulaire.cheminLogo
        modeleIA.fournisseurIA = fourniseurIA
        modeleIADAO.save(modeleIA)
        redirectAttributes.addFlashAttribute(
            "succes", "Le modèle IA a été modifié avec succès."
        )
        return "redirect:/admin/modeles-ia"
    }

    @DeleteMapping("/{id}")
    fun destroy(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        val modeleIA = modeleIADAO.findById(id).orElseThrow {
            ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Modèle IA introuvable"
            )
        }
        if (conversationDAO.existsByModeleIA(modeleIA)) {
            redirectAttributes.addFlashAttribute(
                "erreur",
                "Ce modèle IA est utilisé dans une conversation."
            )
        } else {
            try {
                modeleIADAO.delete(modeleIA)
                redirectAttributes.addFlashAttribute(
                    "succes",
                    "Le modèle IA a été supprimé avec succès."
                )
            } catch (exception: DataIntegrityViolationException) {
                redirectAttributes.addFlashAttribute(
                    "erreur",
                    "Ce modèle IA ne peut pas être supprimé."
                )
            }
        }
        return "redirect:/admin/modeles-ia"
    }
}
