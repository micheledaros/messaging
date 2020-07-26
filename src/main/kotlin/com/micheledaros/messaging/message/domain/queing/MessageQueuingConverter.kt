package com.micheledaros.messaging.message.domain.queing

import com.micheledaros.messaging.message.domain.Message
import com.micheledaros.messaging.user.domain.queuing.UserQueuingConverter
import org.springframework.stereotype.Component

@Component
class MessageQueuingConverter(val userQueuingConverter: UserQueuingConverter) {


    fun toDto(message: Message): MessageQueuingDto {
        return MessageQueuingDto(
                message = message.message,
                sender = userQueuingConverter.toDto(message.sender),
                receiver = userQueuingConverter.toDto(message.receiver))
    }

}