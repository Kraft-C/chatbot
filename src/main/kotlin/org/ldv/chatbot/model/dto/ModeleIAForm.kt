package org.ldv.chatbot.model.dto

data class ModeleIAForm(
    var nom: String = "",
    var urlAPI: String = "",
    var description: String = "",
    var cheminLogo: String = "",
    var fourniseurIAId: Long? = null
)
