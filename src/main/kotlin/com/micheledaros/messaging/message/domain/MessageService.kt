package com.micheledaros.messaging.message.domain

import com.micheledaros.messaging.message.domain.exception.ReceiverIsSameAsSenderException
import com.micheledaros.messaging.message.domain.exception.UnknownUserException
import com.micheledaros.messaging.message.domain.exception.UnknownUserIdException
import com.micheledaros.messaging.security.CurrentUserIdProvider
import com.micheledaros.messaging.user.domain.User
import com.micheledaros.messaging.user.domain.UserRepository
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
        val userRepository: UserRepository,
        val currentUserIdProvider: CurrentUserIdProvider,
        val currentTimeProvider: CurrentTimeProvider

) {

    fun sendMessage(text: String, receiverId: String) : Message{
        val currentUserId =
                currentUserIdProvider.get()
                        ?:let { throw UnknownUserIdException() }

        if (currentUserId == receiverId) {
            throw ReceiverIsSameAsSenderException()
        }

        val currentUser: User = userRepository.findById(currentUserId)
                .orElseThrow{UnknownUserException(currentUserId)}

        val receiver: User = userRepository.findById(receiverId)
                .orElseThrow{UnknownUserException(receiverId)}

        return messageRepository.save(
                Message(
                    message = text,
                    sender = currentUser,
                    receiver = receiver,
                    date = currentTimeProvider.get()
                )
        )
    }



}