package com.micheledaros.messaging.message.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.micheledaros.messaging.configuration.SpringProfiles.H2DB
import com.micheledaros.messaging.configuration.SpringProfiles.LIQUIBASE_OFF
import com.micheledaros.messaging.message.controller.dto.PostMessageDto
import com.micheledaros.messaging.message.domain.MessageMaker.DEFAULT_MESSAGE
import com.micheledaros.messaging.message.domain.MessageService
import com.micheledaros.messaging.user.controller.UserConverter
import com.natpryce.makeiteasy.MakeItEasy.a
import com.natpryce.makeiteasy.MakeItEasy.make
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@ActiveProfiles(H2DB, LIQUIBASE_OFF)
@WebMvcTest(MessageEndpoint::class)
@Import(MessageEndpointConfiguration::class)
internal class MessageEndpointIT (
        @Autowired val mockMvc: MockMvc
){

    private val mapper = ObjectMapper()

    @Autowired
    private lateinit var messageConverter: MessageConverter

    @Autowired
    private lateinit var userConverter: UserConverter

    @Autowired
    private lateinit var messageEndpoint: MessageEndpoint

    @MockBean
    private lateinit var messageService: MessageService

    @Test
    fun `posting to messages creates a message and returns status code 200`() {
        val message = "message"
        val receiverId = "receiver"

        doReturn(make(a(DEFAULT_MESSAGE)))
                .`when`(messageService)
                .sendMessage(message, receiverId)

        val result = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(PostMessageDto(message, receiverId))))
                .andExpect(MockMvcResultMatchers.status().`is`(200))
                .andReturn()

        verify(messageService).sendMessage(message, receiverId)
        assertThat(result.response.contentAsString).isNotEmpty()
    }

    @Test
    fun `getting from messages received withoud specifying sender, limit and startingId returns status code 200`() {
        val result = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/messages/received")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().`is`(200))
                .andReturn()

        assertThat(result.response.contentAsString).isNotEmpty()
    }

    @Test
    fun `getting from messages received specifying all the optional fields returns status code 200`() {
        val senderId = "receiver"

        val result = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/messages/received?senderId=${senderId}&limit=${2}&startingId=${3}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().`is`(200))
                .andReturn()

        assertThat(result.response.contentAsString).isNotEmpty()
    }


}

@TestConfiguration
class MessageEndpointConfiguration {

    @Bean
    fun userConverter() = UserConverter()

    @Bean
    fun messageConverter(userConverter: UserConverter) = MessageConverter(userConverter)

}