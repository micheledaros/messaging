package com.micheledaros.messaging.user.domain

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<User, String> {
    fun findByNickName(nickName:String) : User?
}