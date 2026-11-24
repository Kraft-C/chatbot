package org.ldv.chatbot.service

import org.ldv.chatbot.model.dao.UtilisateurDAO
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val utilisateurDAO: UtilisateurDAO,
) : UserDetailsService {

    override fun loadUserByUsername(
        email: String
    ): UserDetails {

        val utilisateur = utilisateurDAO
            .findByEmailIgnoreCase(email = email)
            ?: throw UsernameNotFoundException(
                "Utilisateur introuvable"
            )

        return User.builder()
            .username(utilisateur.email)
            .password(utilisateur.motDePasse)
            .authorities(utilisateur.role.nomLogique)
            //.disabled(!utilisateur.actif)
            .build()
    }
}
