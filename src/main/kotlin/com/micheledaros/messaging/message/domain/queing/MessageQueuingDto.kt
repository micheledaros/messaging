package com.micheledaros.messaging.message.domain.queing

import com.micheledaros.messaging.user.domain.queuing.UserQueuingDto

data class MessageQueuingDto(
        val message: String,
        val sender: UserQueuingDto,
        val receiver: UserQueuingDto
)