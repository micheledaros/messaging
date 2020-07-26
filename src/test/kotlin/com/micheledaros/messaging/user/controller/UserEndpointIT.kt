package com.micheledaros.messaging.user.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.micheledaros.messaging.user.domain.exception.UserAlreadyExistsException
import com.micheledaros.messaging.user.domain.UserMaker.DEFAULT_USER
import com.micheledaros.messaging.user.domain.UserService
import com.natpryce.makeiteasy.MakeItEasy.a
import com.natpryce.makeiteasy.MakeItEasy.make
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.doThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(UserEndpoint::class)
@Import(UserEndpointTestConfiguration::class)
internal class UserEndpointIT (
        @Autowired val mockMvc: MockMvc
    ){

    private val mapper = ObjectMapper()

    @Autowired
    private lateinit var userConverter: UserConverter
    @MockBean
    private lateinit var userService: UserService

    @Test
    fun `posting to users returns status code 200`() {
        val nickName = "nick"
        doReturn(make(a(DEFAULT_USER))).`when`(userService).createUser(nickName)

        val result = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(NickNameDto(nickName))))
                .andExpect(MockMvcResultMatchers.status().`is`(200))
                .andReturn()

        assertThat(result.response.contentAsString).isNotEmpty()
    }

    @Test
    fun `posting to users with the nickname of an already existing user returns status code 400`() {
        val nickName = "nick"
        doThrow(UserAlreadyExistsException(make(a(DEFAULT_USER)))).`when`(userService).createUser(nickName)

        val result = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(NickNameDto(nickName))))
                .andExpect(MockMvcResultMatchers.status().`is`(400))
                .andReturn()

        assertThat(result.response.contentAsString).isNotEmpty()
    }


}

@TestConfiguration
class UserEndpointTestConfiguration {

    @Bean
    fun userConverter() = UserConverter()

}