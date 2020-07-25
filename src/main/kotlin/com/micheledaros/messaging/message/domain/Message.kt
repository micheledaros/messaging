package com.micheledaros.messaging.message.domain

import com.micheledaros.messaging.infrastructure.generateId
import com.micheledaros.messaging.user.domain.User
import java.util.Date
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "message_t")
data class Message (


        @Column(name = "message", length = 4096)
        val message: String,

        @ManyToOne
        @JoinColumn(name = "sender_id", referencedColumnName = "id", nullable = false)
        val sender : User,

        @ManyToOne
        @JoinColumn(name = "receiver_id", referencedColumnName = "id", nullable = false)
        val receiver: User,

        @Column(name = "created_ad", nullable = false)
        val date: Date
) {
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id : Long = 0
}