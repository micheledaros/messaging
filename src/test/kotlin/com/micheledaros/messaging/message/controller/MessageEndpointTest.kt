package com.micheledaros.messaging.message.controller

import com.micheledaros.messaging.message.controller.MessageDtoMaker.DEFAULT_MESSAGE_DTO
import com.micheledaros.messaging.message.controller.MessagesDtoMaker.DEFAULT_MESSAGES_DTO
import com.micheledaros.messaging.message.controller.dto.PostMessageDto
import com.micheledaros.messaging.message.domain.MessageMaker.DEFAULT_MESSAGE
import com.micheledaros.messaging.message.domain.MessageService
import com.micheledaros.messaging.user.controller.UserConverter
import com.micheledaros.messaging.user.domain.UserMaker.DEFAULT_USER
import com.natpryce.makeiteasy.MakeItEasy.a
import com.natpryce.makeiteasy.MakeItEasy.make
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith (MockitoExtension::class)
internal class MessageEndpointTest {

    @Mock
    private lateinit var messageConverter: MessageConverter
    @Mock
    private lateinit var userConverter: UserConverter
    @Mock
    private lateinit var messageService: MessageService

    @InjectMocks
    private lateinit var messageEndpoint: MessageEndpoint

    @Test
    fun `sendMessage sends the message and returns correctly` (){
        val text = "message"
        val receiverId = "receiver"

        val message = make(a(DEFAULT_MESSAGE))
        doReturn(message).`when`(messageService).sendMessage(text, receiverId)

        val expectedMessageDto = make(a(DEFAULT_MESSAGE_DTO))!!
        doReturn(expectedMessageDto).`when`(messageConverter).toDto(message)

        val returnedMessageDto = messageEndpoint.sendMessage(
                PostMessageDto(text, receiverId))

        assertThat(returnedMessageDto).isEqualTo(expectedMessageDto)
    }

    @Test
    fun `getReceivedMessages without specifying sender returns the right messages`() {
        val startingId: Long = 2
        val limit = 3

        val messages = listOf(make(a(DEFAULT_MESSAGE))!!)
        doReturn(messages).`when`(messageService).loadReceivedMessages(startingId, limit+1)

        val expectedMessagesDto = make(a(DEFAULT_MESSAGES_DTO))
        doReturn(expectedMessagesDto).`when`(messageConverter).convertToDtoTrimmingMessages(messages, limit)
        val returnedMessagesDto = messageEndpoint.getReceivedMessages(startingId = startingId, limit = limit)

        assertThat(returnedMessagesDto).isEqualTo(returnedMessagesDto)
    }

    @Test
    fun `getReceivedMessages specifying sender returns the right messages`() {
        val startingId: Long = 2
        val limit = 3
        val senderId = "senderId"

        val messages = listOf(make(a(DEFAULT_MESSAGE)!!))
        doReturn(messages).`when`(messageService).loadReceivedMessagesFromSender(senderId, startingId, limit+1)

        val expectedMessagesDto = make(a(DEFAULT_MESSAGES_DTO))
        doReturn(expectedMessagesDto).`when`(messageConverter).convertToDtoTrimmingMessages(messages, limit)
        val returnedMessagesDto = messageEndpoint.getReceivedMessages(senderId = senderId, startingId = startingId, limit = limit)

        assertThat(returnedMessagesDto).isEqualTo(returnedMessagesDto)
    }

}