package com.micheledaros.messaging.message.domain

import com.micheledaros.messaging.configuration.SpringProfiles
import com.micheledaros.messaging.message.domain.MessageMaker.DEFAULT_MESSAGE
import com.micheledaros.messaging.message.domain.MessageMaker.MESSAGE
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
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
    private lateinit var messageService: MessageService



    companion object {
        private const val currentUserId = "currentUser_id"
        private const val otherUserId = "otherUserId"


        private val currentUser = make(a(DEFAULT_USER,
                with (ID, currentUserId), with(NICKNAME, "currentUser")))
        private val otherUser = make(a(DEFAULT_USER,
                with (ID, otherUserId), with(NICKNAME, "otherUser")))
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

    @ParameterizedTest
    @MethodSource("provide arguments for testing limit and starting id")
    fun `loadInboundMessages returns the right amount of messages based on limit and starting id`(arguments : ArgumentsForTestingLimitAndStartingId) {
        (1..arguments.totalMessagesCount).forEach{persistMessage("message${it}", otherUser, currentUser)}

        val startingId = arguments.startingIdProvider.invoke()
        val receivedMessages = messageService.loadReceivedMessages(startingId = startingId, limit = arguments.limit)

        assertThat(receivedMessages).hasSize(arguments.expectedLoadedMessagesCount)
    }

    @ParameterizedTest
    @MethodSource("provide arguments for testing limit and starting id")
    fun `loadInboundMessagesFromSender returns the right amount of messages based on limit and starting id`(arguments : ArgumentsForTestingLimitAndStartingId) {
        (1..arguments.totalMessagesCount).forEach{persistMessage("message${it}", otherUser, currentUser)}

        val startingId = arguments.startingIdProvider.invoke()
        val receivedMessages = messageService.loadReceivedMessagesFromSender(
                senderId = otherUserId,
                startingId = startingId,
                limit = arguments.limit)

        assertThat(receivedMessages).hasSize(arguments.expectedLoadedMessagesCount)
    }

    @ParameterizedTest
    @MethodSource("provide arguments for testing limit and starting id")
    fun `loadOutboundMessages returns the right amount messages based on limit and starting id`(arguments : ArgumentsForTestingLimitAndStartingId) {
        (1..arguments.totalMessagesCount).forEach{persistMessage("message${it}", currentUser, otherUser)}

        val startingId = arguments.startingIdProvider.invoke()
        val receivedMessages = messageService.loadSentMessages(
                startingId = startingId,
                limit = arguments.limit)

        assertThat(receivedMessages).hasSize(arguments.expectedLoadedMessagesCount)
    }

    private fun `provide arguments for testing limit and starting id`(): Stream<Arguments?>? {
        return Stream.of(
                Arguments.of(ArgumentsForTestingLimitAndStartingId(3, {-1}, 3, 3)),
                Arguments.of(ArgumentsForTestingLimitAndStartingId(3, {-1}, 2, 2)),
                Arguments.of(ArgumentsForTestingLimitAndStartingId(3, {findMaxId()}, 3, 0)),
                Arguments.of(ArgumentsForTestingLimitAndStartingId(3, {findMaxId()-1}, 3, 1))
        )
    }

    @Test
    fun `loadInboundMessagesFromSender filters the sender correctly`() {
        val thirdUserId = "thirdUserId"
        val thirdUser = make(a(DEFAULT_USER,
                with (ID, thirdUserId), with(NICKNAME, "third_user")))
        userRepository.save(thirdUser)
        persistMessage("message1}", otherUser, currentUser)

        val receivedMessages = messageService.loadReceivedMessagesFromSender(
                senderId = thirdUserId,
                startingId = 0,
                limit = 3)

        assertThat(receivedMessages).isEmpty()
    }

    private fun findMaxId() = messageRepository.findAll().map { it.id }.max()!!

    private fun persistMessage(text:String, sender: User, receiver: User?) {
        val message = make(a(DEFAULT_MESSAGE,
                with(MESSAGE, text),
                with(SENDER, sender),
                with(RECEIVER, receiver)))
        messageRepository.save(message)
    }

    @TestConfiguration
    class MessageServiceITConfiguration {

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
                currentUserIdProvider: CurrentUserIdProvider
                ) = MessageService(
                    messageRepository = messageRepository,
                    userService = userService
        )
    }

    data class ArgumentsForTestingLimitAndStartingId(
            val totalMessagesCount: Int,
            val startingIdProvider : () -> Long,
            val limit: Int,
            val expectedLoadedMessagesCount: Int
    )


}