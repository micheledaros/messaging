package com.micheledaros.messaging.message.domain

import com.micheledaros.messaging.configuration.SpringProfiles
import com.micheledaros.messaging.message.domain.MessageMaker.DEFAULT_MESSAGE
import com.micheledaros.messaging.message.domain.MessageMaker.RECEIVER
import com.micheledaros.messaging.message.domain.MessageMaker.SENDER
import com.micheledaros.messaging.user.domain.CurrentUserIdProvider
import com.micheledaros.messaging.user.domain.User
import com.micheledaros.messaging.user.domain.UserMaker.DEFAULT_USER
import com.micheledaros.messaging.user.domain.UserMaker.ID
import com.micheledaros.messaging.user.domain.UserMaker.NICKNAME
import com.micheledaros.messaging.user.domain.UserRepository
import com.micheledaros.messaging.user.domain.UserService
import com.natpryce.makeiteasy.MakeItEasy.a
import com.natpryce.makeiteasy.MakeItEasy.make
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import java.util.Date

@ActiveProfiles(SpringProfiles.LIQUIBASE_OFF)
@DataJpaTest
internal class MessageServiceIT {

    @Autowired
    private lateinit var messageRepository: MessageRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var currentUserIdProvider: CurrentUserIdProvider

    @Autowired
    private lateinit var currentTimeProvider: CurrentTimeProvider

    @Autowired
    private lateinit var messageService: MessageService


    companion object {
        private val currentTime = Date(1_595_714_631_751)
        private const val currentUserId = "currentUser_id"
        private const val otherUserId = "otherUserId"

        private val currentUser = make(a(DEFAULT_USER,
                with (ID, currentUserId), with(NICKNAME, "currentUser")))
        private val otherUser = make(a(DEFAULT_USER,
                with (ID, otherUserId), with(NICKNAME, "receiver")))

    }

    @BeforeEach
    fun persistUsers() {
        userRepository.save(otherUser)
        userRepository.save(currentUser)
    }

    @Test
    fun `sendMessage persists the messages`() {
        val texts = listOf("message1", "message2", "message3")
        texts.forEach{text-> messageService.sendMessage(text, otherUserId)}

        val allMessages = messageRepository.findAll().toList()
        assertThat(allMessages).hasSize(texts.size)

    }

    @Test
    fun `getReceivedMessages returns the messages`() {

        persistMessage(otherUser, currentUser)

        val maxId = messageRepository.findAll().map { it.id }.max()!!
        val receivedMessages = messageService.getReceivedMessages(startingId = maxId)

        assertThat(receivedMessages).hasSize(1)

    }

    @Test
    fun `getReceivedMessages filters correctly by startingId`() {

        persistMessage(otherUser, currentUser)

        val maxId = messageRepository.findAll().map { it.id }.max()!!

        val receivedMessages = messageService.getReceivedMessages(startingId = maxId+1)

        assertThat(receivedMessages).isEmpty()

    }

    @Test
    fun `getReceivedMessages limits the results correctly as specified`() {

        persistMessage(otherUser, currentUser)
        persistMessage(otherUser, currentUser)
        persistMessage(otherUser, currentUser)

        val limit = 2
        val receivedMessages = messageService.getReceivedMessages(limit = limit)

        assertThat(receivedMessages).hasSize(limit)
    }

    private fun persistMessage(sender: User, receiver: User?) {
        val message = make(a(DEFAULT_MESSAGE,
                with(SENDER, sender),
                with(RECEIVER, receiver)))
        messageRepository.save(message)
    }

    @TestConfiguration
    class MessageServiceITConfiguration {

        @Bean
        fun currentTimeProvider () = object : CurrentTimeProvider (){
            override fun get () : Date {return currentTime}
        }

        @Bean
        fun currentUserIdProvider () = object : CurrentUserIdProvider {
            override fun get () : String {return currentUserId}
        }

        @Bean
        fun userservice(
                userRepository: UserRepository,
                currentUserIdProvider: CurrentUserIdProvider): UserService =
                UserService(userRepository, currentUserIdProvider)

        @Bean
        fun messageService(
                messageRepository: MessageRepository,
                userService: UserService,
                currentUserIdProvider: CurrentUserIdProvider,
                currentTimeProvider: CurrentTimeProvider
                ) = MessageService(
                    messageRepository = messageRepository,
                    userService = userService,
                    currentTimeProvider = currentTimeProvider
        )
    }
}