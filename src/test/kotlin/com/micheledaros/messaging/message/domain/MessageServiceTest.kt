package com.micheledaros.messaging.message.domain

import com.micheledaros.messaging.message.domain.MessageMaker.DEFAULT_MESSAGE
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
import org.springframework.data.domain.PageRequest
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
        const val currentUserId = "sender_id"
        const val otherUserId = "receiver_id"

        private val currentUser = make(a(DEFAULT_USER,
                with (ID, currentUserId), with(NICKNAME, "sender")))
        private val otherUser = make(a(DEFAULT_USER,
                with (ID, otherUserId), with(NICKNAME, "receiver")))

    }

    @Test
    fun `sendMessage persists and returns a new message`() {
        doReturn(currentTime).`when`(currentTimeProvider).get()
        doReturn(currentUserId).`when`(currentUserIdProvider).get()
        doReturn(Optional.of(currentUser)).`when`(userRepository).findById(currentUserId)
        doReturn(Optional.of(otherUser)).`when`(userRepository).findById(otherUserId)
        Mockito.`when`(messageRepository.save(ArgumentMatchers.any<Message>()))
                .then(AdditionalAnswers.returnsFirstArg<User>())

        val message = messageService.sendMessage(text, otherUserId)

        verify(messageRepository).save(message)

        assertThat(message).isEqualToComparingFieldByField(
                Message(
                        text,
                        currentUser,
                        otherUser,
                        currentTime
                ))

    }

    @Test
    fun `sendMessage throws an exception if the current user id is not specified in the request`() {

        doReturn(null).`when`(currentUserIdProvider).get()
        assertThatThrownBy { messageService.sendMessage(text, otherUserId) }
                .isInstanceOf(UnknownUserIdException::class.java)

    }

    @Test
    fun `sendMessage throws an exception if the user specified in the request does not exist`() {

        doReturn(currentUserId).`when`(currentUserIdProvider).get()
        doReturn(Optional.empty<User>()).`when`(userRepository).findById(currentUserId)

        assertThatThrownBy { messageService.sendMessage(text, otherUserId) }
                .isEqualToComparingFieldByField(UnknownUserException(currentUserId))

        verifyNoInteractions(messageRepository)
    }

    @Test
    fun `sendMessage throws an exception if the receiver is the same as the current user`() {

        doReturn(currentUserId).`when`(currentUserIdProvider).get()
        doReturn(Optional.of(currentUser)).`when`(userRepository).findById(currentUserId)

        assertThatThrownBy { messageService.sendMessage(text, currentUserId) }
                .isInstanceOf(ReceiverIsSameAsSenderException::class.java)

        verifyNoInteractions(messageRepository)
    }

    @Test
    fun `sendMessage throws an exception if the receiver does not exist`() {

        doReturn(currentUserId).`when`(currentUserIdProvider).get()
        doReturn(Optional.empty<User>()).`when`(userRepository).findById(otherUserId)
        doReturn(Optional.of(currentUser)).`when`(userRepository).findById(currentUserId)

        assertThatThrownBy { messageService.sendMessage(text, otherUserId) }
                .isEqualToComparingFieldByField(UnknownUserException(otherUserId))

        verifyNoInteractions(messageRepository)
    }

    @Test
    fun `getReceivedMessages returns the correct messages` () {
        doReturn(currentUserId).`when`(currentUserIdProvider).get()
        doReturn(Optional.of(currentUser)).`when`(userRepository).findById(currentUserId)

        val message = make(a(DEFAULT_MESSAGE))

        val startingId: Long = 2
        val limit = 3

        doReturn(listOf(message))
                .`when`(messageRepository)
                .findAllByReceiverAndIdIsGreaterThanEqualOrderById(currentUser, startingId, PageRequest.of(0, limit))

        val receivedMessages = messageService.getReceivedMessages(startingId, limit)

        assertThat(receivedMessages).containsExactly(message)
    }

    @Test
    fun `getReceivedMessages throws an exception if the current user id is not specified in the request`() {

        doReturn(null).`when`(currentUserIdProvider).get()
        assertThatThrownBy { messageService.getReceivedMessages() }
                .isInstanceOf(UnknownUserIdException::class.java)

    }

    @Test
    fun `getReceivedMessages throws an exception if the user specified in the request does not exist`() {

        doReturn(currentUserId).`when`(currentUserIdProvider).get()
        doReturn(Optional.empty<User>()).`when`(userRepository).findById(currentUserId)

        assertThatThrownBy { messageService.getReceivedMessages() }
                .isEqualToComparingFieldByField(UnknownUserException(currentUserId))

    }

}