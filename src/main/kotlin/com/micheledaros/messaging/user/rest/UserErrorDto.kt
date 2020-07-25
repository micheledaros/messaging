package com.micheledaros.messaging.user.rest

data class UserErrorDto(
    val message: String,
    val user: UserDto
)