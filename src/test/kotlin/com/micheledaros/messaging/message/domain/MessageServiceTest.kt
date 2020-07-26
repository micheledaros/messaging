package com.micheledaros.messaging.message.domain

import com.micheledaros.messaging.message.domain.MessageMaker.DEFAULT_MESSAGE
import com.micheledaros.messaging.user.domain.User
import com.micheledaros.messaging.user.domain.UserMaker.DEFAULT_USER
import com.micheledaros.messaging.user.domain.UserMaker.ID
import com.micheledaros.messaging.user.domain.UserMaker.NICKNAME
import com.micheledaros.messaging.user.domain.UserService
import com.natpryce.makeiteasy.MakeItEasy.a
import com.natpryce.makeiteasy.MakeItEasy.make
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.AdditionalAnswers
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageRequest
import java.util.Date
import java.util.Optional

@ExtendWith(MockitoExtension::class)
internal class MessageServiceTest {

    @Mock
    private lateinit var messageRepository: MessageRepository
    @Mock
    private lateinit var userService: UserService
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
                        otherUser,
                        currentTime
                ))

    }


    @Test
    fun `getReceivedMessages returns the correct messages` () {

        doReturn(currentUser).`when`(userService).loadCurrentUser()

        val message = make(a(DEFAULT_MESSAGE))

        val startingId: Long = 2
        val limit = 3

        doReturn(listOf(message))
                .`when`(messageRepository)
                .findAllByReceiverAndIdIsGreaterThanEqualOrderById(currentUser, startingId, PageRequest.of(0, limit))

        val receivedMessages = messageService.getReceivedMessages(startingId, limit)

        assertThat(receivedMessages).containsExactly(message)
    }



}