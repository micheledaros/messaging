package com.micheledaros.messaging.user.domain

import com.micheledaros.messaging.configuration.anyObject
import com.micheledaros.messaging.user.domain.exception.UnknownUserException
import com.micheledaros.messaging.user.domain.exception.UnknownUserIdException
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
import java.util.Optional

@ExtendWith(MockitoExtension::class)
internal class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var currentUserIdProvider: CurrentUserIdProvider

    @InjectMocks
    private lateinit var userService: UserService

    companion object {
        private const val NICKNAME = "nickname"
        private const val USER_ID = "dummyUser"

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

    @Test
    fun `loadUser returns the right user`() {

        val user = make(a(DEFAULT_USER))
        doReturn(Optional.of(user)).`when`(userRepository).findById(USER_ID)

        val currentUser = userService.loadUser(USER_ID)

        assertThat(currentUser).isEqualTo(user)
    }

    @Test
    fun `loadCurrentUser returns the current user`() {

        val user = make(a(DEFAULT_USER))
        doReturn(USER_ID).`when`(currentUserIdProvider).get()
        doReturn(Optional.of(user)).`when`(userRepository).findById(USER_ID)

        val currentUser = userService.loadCurrentUser()

        assertThat(currentUser).isEqualTo(user)
    }

    @Test
    fun `loadUser throws an exception if the specified user does not exist`() {

        doReturn(Optional.empty<User>()).`when`(userRepository).findById(USER_ID)

        assertThatThrownBy { userService.loadUser(USER_ID) }
                .isEqualToComparingFieldByField(UnknownUserException(USER_ID))
    }

    @Test
    fun `loadCurrentUser throws an exception if the current user id is not specified in the request`() {

        doReturn(null).`when`(currentUserIdProvider).get()
        assertThatThrownBy { userService.loadCurrentUser()}
                .isInstanceOf(UnknownUserIdException::class.java)

    }

    @Test
    fun `loadCurrentUser throws an exception if the user specified in the request does not exist`() {

        doReturn(USER_ID).`when`(currentUserIdProvider).get()
        doReturn(Optional.empty<User>()).`when`(userRepository).findById(USER_ID)

        assertThatThrownBy { userService.loadCurrentUser() }
                .isEqualToComparingFieldByField(UnknownUserException(USER_ID))
    }

}