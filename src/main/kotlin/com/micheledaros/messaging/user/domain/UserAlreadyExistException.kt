package com.micheledaros.messaging.user.domain

class UserAlreadyExistException (val user:User): RuntimeException()