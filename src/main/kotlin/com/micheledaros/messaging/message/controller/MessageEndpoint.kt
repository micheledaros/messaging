package com.micheledaros.messaging.message.controller

import com.micheledaros.messaging.message.controller.dto.ErrorMessageDto
import com.micheledaros.messaging.message.controller.dto.MessageDto
import com.micheledaros.messaging.message.controller.dto.MessagesDto
import com.micheledaros.messaging.message.controller.dto.PostMessageDto
import com.micheledaros.messaging.message.domain.Message
import com.micheledaros.messaging.message.domain.MessageService
import com.micheledaros.messaging.message.domain.exception.ReceiverIsSameAsSenderException
import com.micheledaros.messaging.user.controller.UserConverter
import com.micheledaros.messaging.user.controller.dto.UserErrorDto
import com.micheledaros.messaging.user.domain.exception.UnknownUserException
import com.micheledaros.messaging.user.domain.exception.UnknownUserIdException
import com.micheledaros.messaging.user.domain.exception.UserAlreadyExistsException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController


@RestController
class MessageEndpoint (
        val messageService: MessageService,
        val messageConverter: MessageConverter,
        val userConverter: UserConverter
){
    @RequestMapping(
            value = ["/messages"],
            method = [RequestMethod.POST],
            produces = [ MediaType.APPLICATION_JSON_VALUE])
    fun sendMessage(
            @RequestBody
            postMessageDto: PostMessageDto
    ) : MessageDto {
        val message = messageService.sendMessage(postMessageDto.message, postMessageDto.receiverId)
        return messageConverter.toDto(message)
    }

    @RequestMapping(
            value=["/messages/received"],
            method = [RequestMethod.GET],
            produces = [ org.springframework.http.MediaType.APPLICATION_JSON_VALUE]
    )
    fun getReceivedMessages(
            @RequestParam(required = false)
            senderId: String? = null,
            @RequestParam(defaultValue = "50")
            limit: Int,
            @RequestParam(required = false)
            startingId: Long?


    ) : MessagesDto{

        val messages = if (senderId == null) {
            messageService.loadReceivedMessages(startingId, limit+1)
        } else {
            messageService.loadReceivedMessagesFromSender(senderId, startingId, limit+1)
        }

        return messageConverter.convertToDtoTrimmingMessages(messages, limit)
    }

    @RequestMapping(
            value=["/messages/sent"],
            method = [RequestMethod.GET],
            produces = [ org.springframework.http.MediaType.APPLICATION_JSON_VALUE]
    )
    fun getSentMessages(
            @RequestParam(defaultValue = "50")
            limit: Int,
            @RequestParam(required = false)
            startingId: Long?
    ) : MessagesDto{

        val messages =
            messageService.loadSentMessages(startingId, limit+1)
        return messageConverter.convertToDtoTrimmingMessages(messages, limit)
    }


    @ExceptionHandler(ReceiverIsSameAsSenderException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleReceiverIsSameAsSenderException(exception: ReceiverIsSameAsSenderException) : ErrorMessageDto {
        return ErrorMessageDto(
               "receiver and sender can not be the same user"
        )
    }

    @ExceptionHandler(UnknownUserException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleUnknownUserException(exception: UnknownUserException) : ErrorMessageDto {
        return ErrorMessageDto(
                "The user with ID ${exception.id} does not exist in the system"
        )
    }

    @ExceptionHandler(UnknownUserIdException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleUnknownUserIDException(exception: UnknownUserIdException) : ErrorMessageDto {
        return ErrorMessageDto(
                "The user ID has not been specified in the request headers"
        )
    }




}