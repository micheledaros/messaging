package com.micheledaros.messaging.user.domain

import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class UserService (
        val userRepository: UserRepository
) {
    fun createUser (nickname: String) : User {
        userRepository.findByNickName(nickname)?.let { existingUser ->
            throw UserAlreadyExistException(existingUser)
        }
        return userRepository.save(User(nickName = nickname))
    }
}