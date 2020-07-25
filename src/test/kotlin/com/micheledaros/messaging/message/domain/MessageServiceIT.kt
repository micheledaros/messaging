package com.micheledaros.messaging.message.domain

import com.micheledaros.messaging.configuration.SpringProfiles
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
import org.junit.jupiter.api.Test
import org.mockito.AdditionalAnswers
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import java.util.Date
import java.util.Optional

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
        val currentTime = Date(1_595_714_631_751)
        val currentUserId = "currentUser_id"
        val receiverId = "receiver_id"

        private val currentUser = make(a(DEFAULT_USER,
                with (ID, currentUserId), with(NICKNAME, "currentUser")))
        private val receiver = make(a(DEFAULT_USER,
                with (ID, receiverId), with(NICKNAME, "receiver")))

    }

    @Test
    fun `sendMessage persists the messages`() {

        userRepository.save(currentUser)
        userRepository.save(receiver)

        val texts = listOf("message1", "message2", "message3")
        texts.forEach{text-> messageService.sendMessage(text, receiverId)}

        val allMessages = messageRepository.findAll().toList()
        assertThat(allMessages).hasSize(texts.size)

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
        fun messageService(
                messageRepository: MessageRepository,
                userRepository: UserRepository,
                currentUserIdProvider: CurrentUserIdProvider,
                currentTimeProvider: CurrentTimeProvider
                ) = MessageService(
                    messageRepository = messageRepository,
                    userRepository = userRepository,
                    currentUserIdProvider = currentUserIdProvider,
                    currentTimeProvider = currentTimeProvider
        )
    }
}