package com.micheledaros.messaging.user.rest

import com.micheledaros.messaging.user.domain.User
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Component
class UserConverter() {
    fun toDto(user: User) = user.toDto()
}

private fun User.toDto () = UserDto(
        id = id,
        nickName = nickName
)