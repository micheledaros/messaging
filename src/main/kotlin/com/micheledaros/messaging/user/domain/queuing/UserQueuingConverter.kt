package com.micheledaros.messaging.user.domain.queuing

import com.micheledaros.messaging.user.domain.User
import org.springframework.stereotype.Component


@Component
class UserQueuingConverter {
    fun toDto(user: User): UserQueuingDto {
        return UserQueuingDto(user.nickName, user.id)
    }
}