package org.ldv.chatbot.model.dao

import org.ldv.chatbot.model.entity.Role
import org.springframework.data.jpa.repository.JpaRepository

interface RoleDAO : JpaRepository<Role, Long> {
}