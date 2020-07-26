package com.micheledaros.messaging.message.controller

import com.micheledaros.messaging.message.controller.dto.MessageDto
import com.micheledaros.messaging.user.controller.UserDtoMaker
import com.micheledaros.messaging.user.controller.UserDtoMaker.DEFAULT_USERDTO
import com.micheledaros.messaging.user.controller.dto.UserDto
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.a
import com.natpryce.makeiteasy.MakeItEasy.make
import com.natpryce.makeiteasy.MakeItEasy.with

import com.natpryce.makeiteasy.Property

object MessageDtoMaker {
    val ID: Property<MessageDto, Long> = Property.newProperty()
    val MESSAGE: Property<MessageDto, String> = Property.newProperty()
    val SENDER: Property<MessageDto, UserDto> = Property.newProperty()
    val RECEIVER: Property<MessageDto, UserDto> = Property.newProperty()

    val DEFAULT_MESSAGE_DTO: Instantiator<MessageDto> = Instantiator { lookup ->
        MessageDto(
                id = lookup.valueOf(ID, 123456L),
                message = lookup.valueOf(MESSAGE, "dummy text"),
                sender = lookup.valueOf(SENDER, make(a(DEFAULT_USERDTO,
                        with(UserDtoMaker.ID, "sender" ),
                        with(UserDtoMaker.NICKNAME, "sender nick" )
                        ))),
                receiver = lookup.valueOf(RECEIVER, make(a(DEFAULT_USERDTO,
                        with(UserDtoMaker.ID, "sender" ),
                        with(UserDtoMaker.NICKNAME, "sender nick" )
                )))
        )
    }
}