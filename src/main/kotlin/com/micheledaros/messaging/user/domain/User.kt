package com.micheledaros.messaging.user.domain

import com.micheledaros.messaging.infrastructure.generateId
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(name = "user_t")
data class User(
        @Column(name = "id", length = 32)
        @Id
        val id: String = generateId(),

        @Column(name ="nickname", unique = true)
        val nickName: String
)
