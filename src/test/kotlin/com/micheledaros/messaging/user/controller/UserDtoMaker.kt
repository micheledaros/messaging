package com.micheledaros.messaging.user.controller

import com.micheledaros.messaging.user.controller.dto.UserDto
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property

object UserDtoMaker {
    val ID: Property<UserDto, String> = Property.newProperty()
    val NICKNAME: Property<UserDto, String> = Property.newProperty()

    val DEFAULT_USERDTO: Instantiator<UserDto> = Instantiator { lookup ->
        UserDto(
                id = lookup.valueOf(ID, "01234567890123456789012345678901"),
                nickName = lookup.valueOf(NICKNAME, "dummy_test_nickname")
        )
    }
}