package com.micheledaros.messaging.user.domain

import com.micheledaros.messaging.user.domain.exception.UserAlreadyExistsException
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class UserService (
        val userRepository: UserRepository
) {
    fun createUser (nickname: String) : User {
        userRepository.findByNickName(nickname)?.let { existingUser ->
            throw UserAlreadyExistsException(existingUser)
        }
        return userRepository.save(User(nickName = nickname))
    }
}