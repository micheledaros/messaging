package com.micheledaros.messaging

import com.micheledaros.messaging.message.controller.dto.MessageDto
import com.micheledaros.messaging.message.controller.dto.MessagesDto
import com.micheledaros.messaging.message.controller.dto.PostMessageDto
import com.micheledaros.messaging.user.controller.NickNameDto
import com.micheledaros.messaging.user.controller.dto.UserDto
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.annotation.DirtiesContext

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MessagingApplicationEndToEndTest {

	private val userId_header = "userid"


	@LocalServerPort
	var port = 0

	@BeforeEach
	fun setUp() {
		RestAssured.port = port
	}

	@Test
	fun contextLoads() {
	}

	@Test
	fun `a non existing user can create an account providing the nickname`() {
		val nickname = "userA"
		val user = createUser(nickname)
		assertThat(user.id).isNotEmpty()
		assertThat(user.nickName).isEqualTo(nickname)
	}

	@Test
	fun `an user can send a message to another user`() {
		val sender = createUser("userA").id
		val receiver = createUser("userB").id

		val text = "Hi there!"
		val message = sendMessage(text, sender, receiver)
		verifyMessage(message, text, sender, receiver)

	}

	@Test
	fun `an user can see all the messages that he received`() {
		val sender = createUser("userA").id
		val receiver = createUser("userB").id
		val text = "text"
		sendMessage(text, sender, receiver)

		val receivedMessages = getReceivedMessages(receiver)
		val messages = receivedMessages.messages
		assertThat(messages).hasSize(1)
		verifyMessage(messages[0], text, sender, receiver)
	}

	@Test
	fun `an user can see all the messages that he sent`() {
		val sender = createUser("userA").id
		val receiver = createUser("userB").id
		val text = "text"
		sendMessage(text, sender, receiver)

		val receivedMessages = getSentMessages(sender)
		val messages = receivedMessages.messages
		assertThat(messages).hasSize(1)
		verifyMessage(messages[0], text, sender, receiver)
	}

	private fun verifyMessage(message: MessageDto, text: String, sender: String, receiver: String) {
		assertThat(message.message).isEqualTo(text)
		assertThat(message.sender.id).isEqualTo(sender)
		assertThat(message.receiver.id).isEqualTo(receiver)
	}


	fun createUser(nickname: String) : UserDto{
		return given()
				.contentType(ContentType.JSON)
				.body(NickNameDto(nickname))
				.post("/users/")
				.then()
				.statusCode(200)
				.extract()
				.response()
				.body.`as`<UserDto>(UserDto::class.java) as UserDto
	}

	fun sendMessage(message: String, sender:String, receiver:String) : MessageDto{
		return given()
				.contentType(ContentType.JSON)
				.header(userId_header, sender)
				.body(PostMessageDto(message, receiver))
				.post("/messages/")
				.then()
				.statusCode(200)
				.extract()
				.response()
				.body.`as`<MessageDto>(MessageDto::class.java) as MessageDto
	}

	fun getReceivedMessages(user: String) : MessagesDto {
		return given()
				.contentType(ContentType.JSON)
				.header(userId_header, user)
				.get("/messages/received")
				.then()
				.statusCode(200)
				.extract()
				.response()
				.body.`as`<MessageDto>(MessagesDto::class.java) as MessagesDto
	}

	fun getSentMessages(user: String) : MessagesDto {
		return given()
				.contentType(ContentType.JSON)
				.header(userId_header, user)
				.get("/messages/sent")
				.then()
				.statusCode(200)
				.extract()
				.response()
				.body.`as`<MessageDto>(MessagesDto::class.java) as MessagesDto
	}


}
