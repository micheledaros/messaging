package com.micheledaros.messaging.user.controller.dto

data class UserErrorDto(
    val message: String,
    val user: UserDto
)