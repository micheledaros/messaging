package com.micheledaros.messaging.user.domain

import com.micheledaros.messaging.configuration.anyObject
import com.micheledaros.messaging.user.domain.UserMaker.DEFAULT_USER
import com.micheledaros.messaging.user.domain.exception.UserAlreadyExistsException
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

@ExtendWith(MockitoExtension::class)
internal class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var userService: UserService

    companion object {
        private const val NICKNAME = "nickname"
    }


    @Test
    fun `createUser persists and returns a new user with the right nickname and proper id` (){
        Mockito.`when`(userRepository.save(ArgumentMatchers.any<User>()))
                .then(AdditionalAnswers.returnsFirstArg<User>())

        val user = userService.createUser(NICKNAME)

        verify(userRepository).save(user)
        assertThat(user.nickName).isEqualTo(NICKNAME)
        assertThat(user.id.length).isEqualTo(32)
    }

    @Test
    fun `createUser throws an exception an does not create an user if an user with the same nickname is already present` (){
        val existingUser = make(a(DEFAULT_USER))
        doReturn(existingUser).`when`(userRepository).findByNickName(NICKNAME)

        assertThatThrownBy{userService.createUser(NICKNAME)}
                .isEqualToComparingFieldByField(UserAlreadyExistsException(existingUser))

        verify(userRepository, times(0)).save(anyObject())
    }

}