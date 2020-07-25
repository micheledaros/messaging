package com.micheledaros.messaging.message.domain

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : CrudRepository <Message, String> {

}