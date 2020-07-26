package com.micheledaros.messaging.message.controller.dto

import com.micheledaros.messaging.user.controller.dto.UserDto

data class MessageDto (
        val id: Long,
        val message: String,
        val sender: UserDto,
        val receiver: UserDto
)