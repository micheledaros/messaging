package com.micheledaros.messaging.message.domain

import com.micheledaros.messaging.infrastructure.queuing.QueuingService
import com.micheledaros.messaging.message.domain.exception.ReceiverIsSameAsSenderException
import com.micheledaros.messaging.user.domain.User
import com.micheledaros.messaging.user.domain.UserService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import javax.transaction.Transactional


@Service
class MessageService(
        val messageRepository: MessageRepository,
        val userService: UserService,
        val queuingService: QueuingService
) {

    companion object {
        private const val MIN_POSSIBLE_MESSAGE_ID = -1L
        private const val DEFAULT_MESSAGE_LIMIT = 50
    }

    @Transactional
    fun sendMessage(text: String, receiverId: String) : Message{
        val currentUser = userService.loadCurrentUser()

        if (currentUser.id == receiverId) {
            throw ReceiverIsSameAsSenderException()
        }

        val receiver: User = userService.loadUser(receiverId)

        val message = messageRepository.save(
                Message(
                        message = text,
                        sender = currentUser,
                        receiver = receiver
                )
        )
        queuingService.sendMessage(message)
        return message
    }

    fun loadReceivedMessages(startingId:Long?= null, limit:Int?= null) : List<Message>{
        val currentUser = userService.loadCurrentUser()
        return messageRepository.findAllByReceiverAndIdIsGreaterThanOrderByIdDesc(
                currentUser,
                startingId ?: MIN_POSSIBLE_MESSAGE_ID,
                PageRequest.of(0, limit ?: DEFAULT_MESSAGE_LIMIT)
        )
    }

    fun loadReceivedMessagesFromSender(senderId: String, startingId:Long?= null, limit:Int? = null) : List<Message>{
        val currentUser = userService.loadCurrentUser()

        if (currentUser.id == senderId) {
            throw ReceiverIsSameAsSenderException()
        }

        val sender: User = userService.loadUser(senderId)

        return messageRepository.findAllByReceiverAndSenderAndIdIsGreaterThanOrderByIdDesc(
                receiver = currentUser,
                sender = sender,
                startingId = startingId ?: MIN_POSSIBLE_MESSAGE_ID,
                pageable = PageRequest.of(0, limit ?: DEFAULT_MESSAGE_LIMIT)
        )
    }

    fun loadSentMessages(startingId:Long?= null, limit:Int?=null) : List<Message> {
        val currentUser = userService.loadCurrentUser()
        return messageRepository.findAllBySenderAndIdIsGreaterThanOrderByIdDesc(
                currentUser,
                startingId ?: MIN_POSSIBLE_MESSAGE_ID,
                PageRequest.of(0, limit ?: DEFAULT_MESSAGE_LIMIT)
        )
    }
}