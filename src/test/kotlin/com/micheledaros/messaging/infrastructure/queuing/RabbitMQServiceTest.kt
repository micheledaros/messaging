package com.micheledaros.messaging.infrastructure.queuing

import com.fasterxml.jackson.databind.ObjectMapper
import com.micheledaros.messaging.message.domain.MessageMaker.DEFAULT_MESSAGE
import com.micheledaros.messaging.message.domain.queing.MessageQueuingConverter
import com.micheledaros.messaging.message.domain.queing.MessageQueuingDto
import com.micheledaros.messaging.user.domain.queuing.UserQueuingDto
import com.natpryce.makeiteasy.MakeItEasy.a
import com.natpryce.makeiteasy.MakeItEasy.make
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.amqp.rabbit.core.RabbitTemplate

@ExtendWith(MockitoExtension::class)
internal class RabbitMQServiceTest {


    @Mock
    private lateinit var rabbitTemplate: RabbitTemplate

    @Mock
    private lateinit var messageQueuingConverter: MessageQueuingConverter

    @Mock
    private val objectMapper = ObjectMapper()

    val topicExchangeName = "topicExchangeName"
    val routingKey = "routingKey"

    private lateinit var rabbitMQService: RabbitMQService

    @BeforeEach
    fun constructRabbitMQService() {
        rabbitMQService = RabbitMQService(
                rabbitTemplate = rabbitTemplate,
                messageQueuingConverter = messageQueuingConverter,
                objectMapper = objectMapper,
                topicExchangeName = topicExchangeName,
                routingKey = routingKey
        )
    }

    @Test
    fun `sendMessage sends the right message to the queue`() {
        val messageQueuingDto = MessageQueuingDto(
                message = "message",
                sender = UserQueuingDto("senderNick", "senderId"),
                receiver = UserQueuingDto("receiverNick", "reveiverId")
        )

        val message = make(a(DEFAULT_MESSAGE))

        doReturn(messageQueuingDto).`when`(messageQueuingConverter).toDto(message)
        val expectedJson = "json"
        doReturn(expectedJson).`when`(objectMapper).writeValueAsString(messageQueuingDto)

        rabbitMQService.sendMessage(message)

        verify(rabbitTemplate).convertAndSend(topicExchangeName, routingKey, expectedJson)
    }

}