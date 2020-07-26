package com.micheledaros.messaging.message.controller

import com.micheledaros.messaging.configuration.anyObject
import com.micheledaros.messaging.message.controller.dto.MessageDto
import com.micheledaros.messaging.message.domain.Message
import com.micheledaros.messaging.message.domain.MessageMaker.DEFAULT_MESSAGE
import com.micheledaros.messaging.user.controller.UserConverter
import com.micheledaros.messaging.user.controller.UserDtoMaker
import com.micheledaros.messaging.user.controller.UserDtoMaker.DEFAULT_USERDTO
import com.natpryce.makeiteasy.MakeItEasy.a
import com.natpryce.makeiteasy.MakeItEasy.make
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.quality.Strictness
import java.util.stream.Stream

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension::class)
internal class MessageConverterTest {

    @Mock
    private lateinit var userConverter: UserConverter

    @InjectMocks
    private lateinit var messageConverter: MessageConverter

    @Test
    fun `toDto converts all the fields correctly`() {
        val message = make(a(DEFAULT_MESSAGE))
        val receiverDto = make(a(DEFAULT_USERDTO, with(UserDtoMaker.ID, "receiver")))
        val senderDto = make(a(DEFAULT_USERDTO, with(UserDtoMaker.ID, "sender")))

        doReturn(senderDto).`when`(userConverter).toDto(message.sender)
        doReturn (receiverDto).`when`(userConverter).toDto(message.receiver)

        val dto = messageConverter.toDto(message)

        assertThat(dto).isEqualToComparingFieldByField(MessageDto(
                message.id,
                message.message,
                senderDto,
                receiverDto
        ))
    }

    @ParameterizedTest
    @MethodSource("provide arguments for testing trimming of messages")
    fun `toDtoTrimmingMessages trims the messages and sets the hasMore field correctly` (
            messages: List<Message>,
            limit: Int,
            expectedMessages : Int,
            expectedHasMore: Boolean
    ) {
        val userDto = make(a(DEFAULT_USERDTO, with(UserDtoMaker.ID, "receiver")))
        doReturn(userDto).`when`(userConverter).toDto(anyObject())

        val dto = messageConverter.convertToDtoTrimmingMessages(messages, limit)
        assertThat(dto.hasMore).isEqualTo(expectedHasMore)
        assertThat(dto.messages).hasSize(expectedMessages)
    }

    companion object {
        @JvmStatic
        private fun `provide arguments for testing trimming of messages`(): Stream<Arguments?>? {
            val message1 = make(a(DEFAULT_MESSAGE))
            val message2 = make(a(DEFAULT_MESSAGE))
            val message3 = make(a(DEFAULT_MESSAGE))
            val messages = listOf(message1, message2, message3)

            return Stream.of(
                    Arguments.of(messages, 10, 3, false),
                    Arguments.of(messages, 3, 3, false),
                    Arguments.of(messages, 2, 2, true),
                    Arguments.of(messages, 0, 0, true),
                    Arguments.of(emptyList<Message>(), 10, 0, false)

            )

        }
    }

}