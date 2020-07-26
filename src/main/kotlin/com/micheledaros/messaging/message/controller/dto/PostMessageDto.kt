package com.micheledaros.messaging.message.controller.dto

import com.micheledaros.messaging.user.controller.dto.UserDto

data class PostMessageDto (
        val message: String,
        val receiverId: String
)