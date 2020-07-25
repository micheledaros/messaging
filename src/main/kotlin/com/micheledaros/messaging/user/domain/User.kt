package com.micheledaros.messaging.user.domain

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "user_t")
class User(
        @Column(name = "id", length = 32)
        @Id
        val id: String = UUID.randomUUID().toString(),

        @Column(name ="nickname")
        val nickName: String
)
