package com.micheledaros.messaging.user.domain.exception

import com.micheledaros.messaging.user.domain.User

class UserAlreadyExistsException (val user: User): RuntimeException()