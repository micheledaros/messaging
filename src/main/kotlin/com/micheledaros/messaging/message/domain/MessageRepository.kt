package com.micheledaros.messaging.message.domain

import com.micheledaros.messaging.user.domain.User
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : CrudRepository <Message, Long> {
    fun findAllByReceiverAndIdIsGreaterThanOrderByIdDesc(
            receiver: User,
            startingId: Long,
            pageable: Pageable
    ): List<Message>

    fun findAllByReceiverAndSenderAndIdIsGreaterThanOrderByIdDesc(
            receiver: User,
            sender: User,
            startingId: Long,
            pageable: Pageable
    ): List<Message>

    fun findAllBySenderAndIdIsGreaterThanOrderByIdDesc(
            sender: User,
            startingId: Long,
            pageable: Pageable
    ): List<Message>


}