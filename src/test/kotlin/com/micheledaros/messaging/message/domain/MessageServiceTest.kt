package com.micheledaros.messaging.message.domain

import com.micheledaros.messaging.message.domain.exception.ReceiverIsSameAsSenderException
import com.micheledaros.messaging.message.domain.exception.UnknownUserException
import com.micheledaros.messaging.message.domain.exception.UnknownUserIdException
import com.micheledaros.messaging.security.CurrentUserIdProvider
import com.micheledaros.messaging.user.domain.User
import com.micheledaros.messaging.user.domain.UserMaker.DEFAULT_USER
import com.micheledaros.messaging.user.domain.UserMaker.ID
import com.micheledaros.messaging.user.domain.UserMaker.NICKNAME
import com.micheledaros.messaging.user.domain.UserRepository
import com.natpryce.makeiteasy.MakeItEasy.a
import com.natpryce.makeiteasy.MakeItEasy.make
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.Condition
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.AdditionalAnswers
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Date
import java.util.Optional

@ExtendWith(MockitoExtension::class)
internal class MessageServiceTest {

    @Mock
    private lateinit var messageRepository: MessageRepository
    @Mock
    private lateinit var userRepository: UserRepository
    @Mock
    private lateinit var currentUserIdProvider: CurrentUserIdProvider
    @Mock
    private lateinit var currentTimeProvider: CurrentTimeProvider

    @InjectMocks
    private lateinit var messageService: MessageService


    companion object {
        const val text = "hi there!"
        val currentTime = Date(1_595_714_631_751)
        const val senderId = "sender_id"
        const val receiverId = "receiver_id"

        private val sender = make(a(DEFAULT_USER,
                with (ID, senderId), with(NICKNAME, "sender")))
        private val receiver = make(a(DEFAULT_USER,
                with (ID, receiverId), with(NICKNAME, "receiver")))

    }

    @Test
    fun `sendMessage persists and returns a new message`() {
        doReturn(currentTime).`when`(currentTimeProvider).get()
        doReturn(senderId).`when`(currentUserIdProvider).get()
        doReturn(Optional.of(sender)).`when`(userRepository).findById(senderId)
        doReturn(Optional.of(receiver)).`when`(userRepository).findById(receiverId)
        Mockito.`when`(messageRepository.save(ArgumentMatchers.any<Message>()))
                .then(AdditionalAnswers.returnsFirstArg<User>())

        val message = messageService.sendMessage(text, receiverId)

        verify(messageRepository).save(message)

        assertThat(message).isEqualToComparingFieldByField(
                Message(
                        text,
                        sender,
                        receiver,
                        currentTime
                ))

    }

    @Test
    fun `sendMessage throws an exception if the current user id is not specified in the request`() {

        doReturn(null).`when`(currentUserIdProvider).get()
        assertThatThrownBy { messageService.sendMessage(text, receiverId) }
                .isInstanceOf(UnknownUserIdException::class.java)

    }

    @Test
    fun `sendMessage throws an exeception if the receiver is the same as the current user`() {

        doReturn(senderId).`when`(currentUserIdProvider).get()

        assertThatThrownBy { messageService.sendMessage(text, senderId) }
                .isInstanceOf(ReceiverIsSameAsSenderException::class.java)

        verifyNoInteractions(messageRepository)
    }

    @Test
    fun `sendMessage throws an exeception if the user specified in the request does not exist`() {

        doReturn(senderId).`when`(currentUserIdProvider).get()
        doReturn(Optional.empty<User>()).`when`(userRepository).findById(senderId)

        assertThatThrownBy { messageService.sendMessage(text, receiverId) }
                .isEqualToComparingFieldByField(UnknownUserException(senderId))

        verifyNoInteractions(messageRepository)
    }

    @Test
    fun `sendMessage throws an exception if the receiver does not exist`() {

        doReturn(senderId).`when`(currentUserIdProvider).get()
        doReturn(Optional.empty<User>()).`when`(userRepository).findById(receiverId)
        doReturn(Optional.of(sender)).`when`(userRepository).findById(senderId)

        assertThatThrownBy { messageService.sendMessage(text, receiverId) }
                .isEqualToComparingFieldByField(UnknownUserException(receiverId))

        verifyNoInteractions(messageRepository)
    }

}