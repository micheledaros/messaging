package com.micheledaros.messaging.message.domain

import com.micheledaros.messaging.message.domain.exception.ReceiverIsSameAsSenderException
import com.micheledaros.messaging.user.domain.User
import com.micheledaros.messaging.user.domain.UserService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.util.Date

@Component
class CurrentTimeProvider {
    fun get() = Date()
}

@Service
class MessageService (
        val messageRepository: MessageRepository,
        val currentTimeProvider: CurrentTimeProvider,
        val userService: UserService
) {

    fun sendMessage(text: String, receiverId: String) : Message{
        val currentUser = userService.loadCurrentUser()

        if (currentUser.id == receiverId) {
            throw ReceiverIsSameAsSenderException()
        }

        val receiver: User = userService.loadUser(receiverId)

        return messageRepository.save(
                Message(
                    message = text,
                    sender = currentUser,
                    receiver = receiver,
                    date = currentTimeProvider.get()
                )
        )
    }

    fun getReceivedMessages(startingId:Long=0, limit:Int=50) : List<Message>{
        val currentUser = userService.loadCurrentUser()
        return  messageRepository.findAllByReceiverAndIdIsGreaterThanEqualOrderById(
               currentUser,
               startingId,
               PageRequest.of(0,limit)
        )
    }
}