package com.micheledaros.messaging.message.domain.queing

import com.micheledaros.messaging.message.domain.MessageMaker.DEFAULT_MESSAGE
import com.micheledaros.messaging.user.domain.queuing.UserQueuingConverter
import com.micheledaros.messaging.user.domain.queuing.UserQueuingDto
import com.natpryce.makeiteasy.MakeItEasy.a
import com.natpryce.makeiteasy.MakeItEasy.make
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class MessageQueuingConverterTest {

    val messageQueuingConverter = MessageQueuingConverter(UserQueuingConverter())

    @Test
    fun `toDto converts all the fields correctly`() {
        val message = make(a(DEFAULT_MESSAGE))

        val messageQueuingDto = messageQueuingConverter.toDto(message)

        assertThat(messageQueuingDto).isEqualToComparingFieldByField(
                MessageQueuingDto(
                        message = message.message,
                        sender = UserQueuingDto(
                                nickName = message.sender.nickName,
                                id = message.sender.id),
                        receiver = UserQueuingDto(
                                nickName = message.receiver.nickName,
                                id = message.receiver.id
                        ))
        )
    }
}