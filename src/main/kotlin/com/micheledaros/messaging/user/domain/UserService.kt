package com.micheledaros.messaging.user.domain

import com.micheledaros.messaging.user.domain.exception.UnknownUserException
import com.micheledaros.messaging.user.domain.exception.UnknownUserIdException
import com.micheledaros.messaging.user.domain.exception.UserAlreadyExistsException
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class UserService (
        val userRepository: UserRepository,
        val currentUserIdProvider: CurrentUserIdProvider) {

    fun createUser (nickname: String) : User {
        userRepository.findByNickName(nickname)?.let { existingUser ->
            throw UserAlreadyExistsException(existingUser)
        }
        return userRepository.save(User(nickName = nickname))
    }

    fun loadUser(receiverId: String): User {
        return userRepository.findById(receiverId)
                .orElseThrow { UnknownUserException(receiverId) }
    }

    fun loadCurrentUser(): User {
        val currentUserId =
                currentUserIdProvider.get()
                        ?: let { throw UnknownUserIdException() }

        return loadUser(currentUserId)
    }
}