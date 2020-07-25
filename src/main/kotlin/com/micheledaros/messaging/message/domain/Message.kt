package com.micheledaros.messaging.message.domain

import com.micheledaros.messaging.infrastructure.generateId
import com.micheledaros.messaging.user.domain.User
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "message_t")
data class Message (
        @Column(name = "id", length = 32)
        @Id
        val id: String = generateId(),

        @ManyToOne
        @JoinColumn(name = "sender_id", referencedColumnName = "id", nullable = false)
        val sender : User,

        @ManyToOne
        @JoinColumn(name = "receiver_id", referencedColumnName = "id", nullable = false)
        val receiver: User
)