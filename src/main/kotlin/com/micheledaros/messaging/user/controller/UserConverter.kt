package com.micheledaros.messaging.user.controller

import com.micheledaros.messaging.user.controller.dto.UserDto
import com.micheledaros.messaging.user.domain.User
import org.springframework.stereotype.Component

@Component
class UserConverter() {
    fun toDto(user: User) = user.toDto()
}

private fun User.toDto () = UserDto(
        id = id,
        nickName = nickName
)