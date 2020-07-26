package com.micheledaros.messaging.user.controller

import com.micheledaros.messaging.user.domain.exception.UserAlreadyExistsException
import com.micheledaros.messaging.user.domain.UserMaker
import com.micheledaros.messaging.user.domain.UserMaker.DEFAULT_USER
import com.micheledaros.messaging.user.domain.UserService
import com.micheledaros.messaging.user.controller.UserDtoMaker.DEFAULT_USERDTO
import com.micheledaros.messaging.user.controller.dto.UserErrorDto
import com.natpryce.makeiteasy.MakeItEasy.a
import com.natpryce.makeiteasy.MakeItEasy.make
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith (MockitoExtension::class)
internal class UserEndpointTest {


    @Mock
    private lateinit var userConverter: UserConverter
    @Mock
    private lateinit var userService: UserService
    @InjectMocks
    private lateinit var userEndpoint: UserEndpoint

    @Test
    fun `createUser calls the service and returns correctly` ()  {
        val nickName = "nick"
        val user = make(a(DEFAULT_USER, with(UserMaker.NICKNAME, nickName)))
        val userDto = make(a(DEFAULT_USERDTO, with(UserDtoMaker.NICKNAME, nickName)))

        doReturn(userDto).`when`(userConverter).toDto(user)
        doReturn(user).`when`(userService).createUser(nickName)

        val createdUser = userEndpoint.createUser(NickNameDto(nickName))

        assertThat(createdUser).isEqualTo(userDto)
    }

    @Test
    fun `handleUserAlreadyExistException returns the correct dto` ()  {
        val nickName = "nick"
        val user = make(a(DEFAULT_USER, with(UserMaker.NICKNAME, nickName)))
        val userDto = make(a(DEFAULT_USERDTO, with(UserDtoMaker.NICKNAME, nickName)))
        doReturn(userDto).`when`(userConverter).toDto(user)

        val userErrorDto = userEndpoint.handleUserAlreadyExistException(UserAlreadyExistsException(user))

        assertThat(userErrorDto).isEqualTo(
                UserErrorDto(
                        "an user with the same nickname is already existing",
                        userDto
                )
        )

    }



}