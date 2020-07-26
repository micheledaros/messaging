package com.micheledaros.messaging.message.domain

import com.micheledaros.messaging.infrastructure.queuing.QueuingService
import com.micheledaros.messaging.message.domain.MessageMaker.DEFAULT_MESSAGE
import com.micheledaros.messaging.message.domain.exception.ReceiverIsSameAsSenderException
import com.micheledaros.messaging.user.domain.User
import com.micheledaros.messaging.user.domain.UserMaker.DEFAULT_USER
import com.micheledaros.messaging.user.domain.UserMaker.ID
import com.micheledaros.messaging.user.domain.UserMaker.NICKNAME
import com.micheledaros.messaging.user.domain.UserService
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

@ExtendWith(MockitoExtension::class)
internal class MessageServiceTest {

    @Mock
    private lateinit var messageRepository: MessageRepository

    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var queuingService: QueuingService

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

        private val message = make(a(DEFAULT_MESSAGE))

    }

    @Test
    fun `sendMessage persists and returns a new message`() {
        doReturn(currentUser).`when`(userService).loadCurrentUser()
        doReturn(otherUser).`when`(userService).loadUser(otherUserId)
        Mockito.`when`(messageRepository.save(ArgumentMatchers.any<Message>()))
                .then(AdditionalAnswers.returnsFirstArg<User>())

        val message = messageService.sendMessage(text, otherUserId)

        verify(messageRepository).save(message)

        assertThat(message).isEqualToComparingFieldByField(
                Message(
                        text,
                        currentUser,
                        otherUser
                ))
    }

    @Test
    fun `sendMessage sends a message to a queue`() {
        doReturn(currentUser).`when`(userService).loadCurrentUser()
        doReturn(otherUser).`when`(userService).loadUser(otherUserId)
        Mockito.`when`(messageRepository.save(ArgumentMatchers.any<Message>()))
                .then(AdditionalAnswers.returnsFirstArg<User>())

        val message = messageService.sendMessage(text, otherUserId)

        verify(queuingService).sendMessage(message)
    }

    @Test
    fun `sendMessage to self throws an exception`() {
        doReturn(currentUser).`when`(userService).loadCurrentUser()

        assertThatThrownBy { messageService.sendMessage(text, currentUserId) }
                .isInstanceOf(ReceiverIsSameAsSenderException::class.java)
        verifyNoInteractions(messageRepository)
    }

    @Test
    fun `loadInboundMessages returns the correct messages` () {
        doReturn(currentUser).`when`(userService).loadCurrentUser()
        val startingId: Long = 2
        val limit = 3

        doReturn(listOf(message))
                .`when`(messageRepository)
                .findAllByReceiverAndIdIsGreaterThanOrderByIdDesc(currentUser, startingId, PageRequest.of(0, limit))

        val receivedMessages = messageService.loadReceivedMessages(startingId, limit)

        assertThat(receivedMessages).containsExactly(message)
    }

    @Test
    fun `loadInboundMessages has the right default parameters` () {
        doReturn(currentUser).`when`(userService).loadCurrentUser()

        messageService.loadReceivedMessages()

        verify(messageRepository)
                .findAllByReceiverAndIdIsGreaterThanOrderByIdDesc(currentUser, -1, PageRequest.of(0, 50))
    }

    @Test
    fun `loadInboundMessagesFromSender returns the correct messages` () {
        doReturn(currentUser).`when`(userService).loadCurrentUser()
        doReturn(otherUser).`when`(userService).loadUser(otherUserId)
        val startingId: Long = 2
        val limit = 3

        doReturn(listOf(message))
                .`when`(messageRepository)
                .findAllByReceiverAndSenderAndIdIsGreaterThanOrderByIdDesc(currentUser, otherUser, startingId, PageRequest.of(0, limit))

        val receivedMessages = messageService.loadReceivedMessagesFromSender(otherUserId, startingId, limit)

        assertThat(receivedMessages).containsExactly(message)
    }


    @Test
    fun `loadInboundMessagesFromSender from self throws an exception` () {
        doReturn(currentUser).`when`(userService).loadCurrentUser()

        assertThatThrownBy { messageService.loadReceivedMessagesFromSender(currentUserId) }
                .isInstanceOf(ReceiverIsSameAsSenderException::class.java)
    }

    @Test
    fun `loadInboundMessagesFromSender has the right default parameters` () {
        doReturn(currentUser).`when`(userService).loadCurrentUser()
        doReturn(otherUser).`when`(userService).loadUser(otherUserId)

        messageService.loadReceivedMessagesFromSender(otherUserId)

        verify(messageRepository)
                .findAllByReceiverAndSenderAndIdIsGreaterThanOrderByIdDesc(currentUser, otherUser, -1, PageRequest.of(0, 50))
    }

    @Test
    fun `loadOutboundMessages returns the correct messages` () {
        doReturn(currentUser).`when`(userService).loadCurrentUser()
        val startingId: Long = 2
        val limit = 3

        doReturn(listOf(message))
                .`when`(messageRepository)
                .findAllBySenderAndIdIsGreaterThanOrderByIdDesc(currentUser, startingId, PageRequest.of(0, limit))

        val receivedMessages = messageService.loadSentMessages(startingId, limit)

        assertThat(receivedMessages).containsExactly(message)
    }

    @Test
    fun `loadOutboundMessages has the right default parameters` () {
        doReturn(currentUser).`when`(userService).loadCurrentUser()

        messageService.loadSentMessages()

        verify(messageRepository)
                .findAllBySenderAndIdIsGreaterThanOrderByIdDesc(currentUser, -1, PageRequest.of(0, 50))
    }

}