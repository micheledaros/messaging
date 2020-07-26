package com.micheledaros.messaging.message.domain

import com.micheledaros.messaging.user.domain.User
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : CrudRepository <Message, Long> {
    fun findAllByReceiverAndIdIsGreaterThanOrderById(
            receiver: User,
            startingId:Long,
            pageable: Pageable
    ): List<Message>

    fun findAllByReceiverAndSenderAndIdIsGreaterThanOrderById(
            receiver: User,
            sender: User,
            startingId:Long,
            pageable: Pageable
    ): List<Message>

    fun findAllBySenderAndIdIsGreaterThanOrderById(
            sender: User,
            startingId:Long,
            pageable: Pageable
    ): List<Message>


}