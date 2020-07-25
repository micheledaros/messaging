package com.micheledaros.messaging.user.domain

import com.micheledaros.messaging.configuration.SpringProfiles.LIQUIBASE_OFF
import com.micheledaros.messaging.configuration.anyObject
import com.micheledaros.messaging.user.domain.UserMaker.DEFAULT_USER
import com.natpryce.makeiteasy.MakeItEasy.a
import com.natpryce.makeiteasy.MakeItEasy.make
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
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import java.util.Arrays.asList

@ActiveProfiles(LIQUIBASE_OFF)
@DataJpaTest
internal class UserServiceIT {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userService: UserService

   @Test
   fun `createUser persists a user correctly` () {
       val nicknames = listOf("nickname1", "nickname2", "nikname3")
       nicknames.forEach{userService.createUser(it)}

       assertThat(userRepository.findAll().map { it.nickName })
               .containsExactlyInAnyOrderElementsOf(nicknames)
   }

   @TestConfiguration
   class UserServiceITConfiguration {
       @Bean
       fun userService (userRepository: UserRepository) = UserService(userRepository)
   }




}