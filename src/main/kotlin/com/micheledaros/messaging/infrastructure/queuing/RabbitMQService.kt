package com.micheledaros.messaging.infrastructure.queuing

import com.fasterxml.jackson.databind.ObjectMapper
import com.micheledaros.messaging.message.domain.Message
import com.micheledaros.messaging.message.domain.queing.MessageQueuingConverter
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class RabbitMQService(
        val rabbitTemplate: RabbitTemplate,
        val messageQueuingConverter: MessageQueuingConverter,
        val objectMapper: ObjectMapper,
        @Value("queuing.topicExchangeName")
        val topicExchangeName: String,
        @Value("queuing.routingKey")
        val routingKey: String
) : QueuingService {


    override fun sendMessage(message: Message) {
        val messageQueuingDto = messageQueuingConverter.toDto(message)
        val jsonRepresentation = objectMapper.writeValueAsString(messageQueuingDto)
        rabbitTemplate.convertAndSend(topicExchangeName, routingKey, jsonRepresentation)
    }

}