package com.micheledaros.messaging.user.controller

import com.micheledaros.messaging.user.controller.dto.UserDto
import com.micheledaros.messaging.user.controller.dto.UserErrorDto
import com.micheledaros.messaging.user.domain.exception.UserAlreadyExistsException
import com.micheledaros.messaging.user.domain.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class UserEndpoint (
        val userService: UserService,
        val userConverter: UserConverter
){

    @RequestMapping(
            value = ["/users"],
            method = [RequestMethod.POST],
            produces = [ MediaType.APPLICATION_JSON_VALUE])
    fun createUser(
            @RequestBody
            nickNameDto:NickNameDto
    ) : UserDto {
        val user = userService.createUser(nickNameDto.nickName)
        return userConverter.toDto(user)
    }

    @ExceptionHandler(UserAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleUserAlreadyExistException(exception: UserAlreadyExistsException) : UserErrorDto {
        val existingUser = userConverter.toDto(exception.user)
        return UserErrorDto(
                message = "an user with the same nickname is already existing",
                //I assume that the service is called only by "trusted" sources,
                //so it is safe to return the id of the existing user
                user = existingUser
        )
    }

}

data class NickNameDto (val nickName: String)