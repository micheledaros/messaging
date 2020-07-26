package com.micheledaros.messaging.user.domain

import com.micheledaros.messaging.configuration.SpringProfiles.LIQUIBASE_OFF
import com.micheledaros.messaging.user.domain.UserMaker.DEFAULT_USER
import com.micheledaros.messaging.user.domain.UserMaker.ID
import com.natpryce.makeiteasy.MakeItEasy.a
import com.natpryce.makeiteasy.MakeItEasy.make
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles(LIQUIBASE_OFF)
@DataJpaTest
internal class UserServiceIT {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userService: UserService

    companion object {
        private const val currentUserId = "currentUser_id"
    }

   @Test
   fun `createUser persists a user correctly` () {
       val nicknames = listOf("nickname1", "nickname2", "nikname3")
       nicknames.forEach{userService.createUser(it)}

       assertThat(userRepository.findAll().map { it.nickName })
               .containsExactlyInAnyOrderElementsOf(nicknames)
   }

    @Test
    fun `loadUser loads a user correctly` () {
        val persistedUser = userRepository.save(make(a(DEFAULT_USER)))

        val loadedUser = userService.loadUser(persistedUser.id)

        assertThat(loadedUser)
                .isEqualTo(persistedUser)
    }

    @Test
    fun `loadCurrentUser loads the current user correctly` () {
        val persistedUser =
                userRepository.save(make(a(DEFAULT_USER,
                        with(ID, currentUserId))))

        val loadedUser = userService.loadCurrentUser()

        assertThat(loadedUser)
                .isEqualTo(persistedUser)
    }


   @TestConfiguration
   class UserServiceITConfiguration {

       @Bean
       fun currentUserIdProvider () = object : CurrentUserIdProvider {
                override fun get () : String {return currentUserId}
           }


       @Bean
       fun userService (
               userRepository: UserRepository,
               currentUserIdProvider: CurrentUserIdProvider
       ) = UserService(userRepository, currentUserIdProvider)
   }




}