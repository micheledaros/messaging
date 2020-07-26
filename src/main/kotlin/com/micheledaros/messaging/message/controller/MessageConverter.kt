package com.micheledaros.messaging.message.controller

import com.micheledaros.messaging.message.controller.dto.MessageDto
import com.micheledaros.messaging.message.controller.dto.MessagesDto
import com.micheledaros.messaging.message.domain.Message
import com.micheledaros.messaging.user.controller.UserConverter
import org.springframework.stereotype.Component

@Component
class MessageConverter (
        val userConverter: UserConverter
) {
    fun toDto(message: Message) = MessageDto(
            id = message.id,
            message = message.message,
            sender = userConverter.toDto(message.sender),
            receiver = userConverter.toDto(message.receiver)
    )

    fun convertToDtoTrimmingMessages(messages: List<Message>, limit: Int) :MessagesDto {
        val messageDtos = messages.map ( this::toDto )
        val trimmed =  (messageDtos.size > limit)
        if (trimmed) {
            return MessagesDto(messageDtos.subList(0,limit), true)
        } else {
            return MessagesDto(messageDtos, false)
        }
    }

}

