package org.ldv.chatbot.controller.admin

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.ldv.chatbot.model.dao.ConversationDAO
import org.ldv.chatbot.model.dao.FournisseurIADAO
import org.ldv.chatbot.model.dao.ModeleIADAO
import org.ldv.chatbot.model.entity.Conversation
import org.ldv.chatbot.model.entity.FournisseurIA
import org.ldv.chatbot.model.entity.ModeleIA
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@Transactional
class AdminModeleIAControllerTest {

    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var modeleIADAO: ModeleIADAO

    @Autowired
    private lateinit var fournisseurIADAO: FournisseurIADAO

    @Autowired
    private lateinit var conversationDAO: ConversationDAO

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .apply<DefaultMockMvcBuilder>(springSecurity())
            .build()
    }

    @Test
    fun `acces refuse pour utilisateur anonyme`() {
        mockMvc.perform(get("/admin/modeles-ia"))
            .andExpect(status().is3xxRedirection)
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `acces refuse pour role USER`() {
        mockMvc.perform(get("/admin/modeles-ia"))
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `acces autorise pour role ADMIN sur index`() {
        mockMvc.perform(get("/admin/modeles-ia"))
            .andExpect(status().isOk)
            .andExpect(view().name("admin/modeleIA/index"))
            .andExpect(model().attributeExists("modelesIA"))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `affichage du formulaire create`() {
        mockMvc.perform(get("/admin/modeles-ia/create"))
            .andExpect(status().isOk)
            .andExpect(view().name("admin/modeleIA/create"))
            .andExpect(model().attributeExists("fourniseursIA"))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `creation dun modele IA avec store`() {
        val fournisseur = fournisseurIADAO.findAll().first()
        mockMvc.perform(
            post("/admin/modeles-ia")
                .with(csrf())
                .param("nom", "test-gpt-5")
                .param("urlAPI", "https://api.openai.com/v1/chat")
                .param("description", "Super modele test")
                .param("cheminLogo", "/img/test.png")
                .param("fourniseurIAId", fournisseur.id.toString())
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/admin/modeles-ia"))
            .andExpect(flash().attribute("succes", "Le modèle IA a été créé avec succès."))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `consultation show dun modele IA existant`() {
        val fournisseur = fournisseurIADAO.findAll().first()
        val modele = modeleIADAO.save(
            ModeleIA(
                nom = "test-show",
                urlAPI = "https://example.com/api",
                description = "Desc",
                cheminLogo = "/img/logo.png",
                fournisseurIA = fournisseur
            )
        )

        mockMvc.perform(get("/admin/modeles-ia/${modele.id}"))
            .andExpect(status().isOk)
            .andExpect(view().name("admin/modeleIA/show"))
            .andExpect(model().attributeExists("modeleIA"))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `show avec identifiant inconnu renvoie 404`() {
        mockMvc.perform(get("/admin/modeles-ia/999999"))
            .andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `affichage edit dun modele IA existant`() {
        val fournisseur = fournisseurIADAO.findAll().first()
        val modele = modeleIADAO.save(
            ModeleIA(
                nom = "test-edit",
                urlAPI = "https://example.com/api",
                description = "Desc",
                cheminLogo = "/img/logo.png",
                fournisseurIA = fournisseur
            )
        )

        mockMvc.perform(get("/admin/modeles-ia/${modele.id}/edit"))
            .andExpect(status().isOk)
            .andExpect(view().name("admin/modeleIA/edit"))
            .andExpect(model().attributeExists("modeleIA"))
            .andExpect(model().attributeExists("fourniseursIA"))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `modification update dun modele IA avec PUT`() {
        val fournisseur = fournisseurIADAO.findAll().first()
        val modele = modeleIADAO.save(
            ModeleIA(
                nom = "test-update",
                urlAPI = "https://example.com/api",
                description = "Desc",
                cheminLogo = "/img/logo.png",
                fournisseurIA = fournisseur
            )
        )

        mockMvc.perform(
            put("/admin/modeles-ia/${modele.id}")
                .with(csrf())
                .param("nom", "test-update-modifie")
                .param("urlAPI", "https://example.com/api/v2")
                .param("description", "Desc modifiee")
                .param("cheminLogo", "/img/logo2.png")
                .param("fourniseurIAId", fournisseur.id.toString())
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/admin/modeles-ia"))
            .andExpect(flash().attribute("succes", "Le modèle IA a été modifié avec succès."))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `suppression delete dun modele IA sans conversation`() {
        val fournisseur = fournisseurIADAO.findAll().first()
        val modele = modeleIADAO.save(
            ModeleIA(
                nom = "test-delete",
                urlAPI = "https://example.com/api",
                description = "Desc",
                cheminLogo = "/img/logo.png",
                fournisseurIA = fournisseur
            )
        )

        assertFalse(conversationDAO.existsByModeleIA(modele))

        mockMvc.perform(
            delete("/admin/modeles-ia/${modele.id}")
                .with(csrf())
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/admin/modeles-ia"))
            .andExpect(flash().attribute("succes", "Le modèle IA a été supprimé avec succès."))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `suppression dun modele IA utilise dans une conversation est refusee`() {
        val fournisseur = fournisseurIADAO.findAll().first()
        val modele = modeleIADAO.save(
            ModeleIA(
                nom = "test-delete-used",
                urlAPI = "https://example.com/api",
                description = "Desc",
                cheminLogo = "/img/logo.png",
                fournisseurIA = fournisseur
            )
        )

        val conv = Conversation(
            titre = "Conversation test",
            modeleIA = modele
        )
        conversationDAO.save(conv)

        assertTrue(conversationDAO.existsByModeleIA(modele))

        mockMvc.perform(
            delete("/admin/modeles-ia/${modele.id}")
                .with(csrf())
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/admin/modeles-ia"))
            .andExpect(flash().attribute("erreur", "Ce modèle IA est utilisé dans une conversation."))
    }
}
