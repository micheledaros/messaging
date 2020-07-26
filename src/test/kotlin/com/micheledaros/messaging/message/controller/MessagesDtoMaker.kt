package com.micheledaros.messaging.message.controller

import com.micheledaros.messaging.message.controller.MessageDtoMaker.DEFAULT_MESSAGE_DTO
import com.micheledaros.messaging.message.controller.dto.MessageDto
import com.micheledaros.messaging.message.controller.dto.MessagesDto
import com.micheledaros.messaging.user.controller.UserDtoMaker
import com.micheledaros.messaging.user.controller.UserDtoMaker.DEFAULT_USERDTO
import com.micheledaros.messaging.user.controller.dto.UserDto
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.a
import com.natpryce.makeiteasy.MakeItEasy.make
import com.natpryce.makeiteasy.MakeItEasy.with

import com.natpryce.makeiteasy.Property

object MessagesDtoMaker {
    val HAS_MORE: Property<MessagesDto, Boolean> = Property.newProperty()
    val MESSAGES: Property<MessagesDto, List<MessageDto>> = Property.newProperty()

    val DEFAULT_MESSAGES_DTO: Instantiator<MessagesDto> = Instantiator { lookup ->
        MessagesDto(
           hasMore = lookup.valueOf(HAS_MORE, true),
           messages = lookup.valueOf(MESSAGES, listOf(make(a(DEFAULT_MESSAGE_DTO))))
        )
    }
}