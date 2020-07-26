package com.micheledaros.messaging.message.domain

import com.micheledaros.messaging.user.domain.User
import com.micheledaros.messaging.user.domain.UserMaker.DEFAULT_USER
import com.micheledaros.messaging.user.domain.UserMaker.ID
import com.micheledaros.messaging.user.domain.UserMaker.NICKNAME
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.a
import com.natpryce.makeiteasy.MakeItEasy.make
import com.natpryce.makeiteasy.MakeItEasy.with

import com.natpryce.makeiteasy.Property
import java.util.Date

object MessageMaker {
    val MESSAGE: Property<Message, String> = Property.newProperty()
    val SENDER: Property<Message, User> = Property.newProperty()
    val RECEIVER: Property<Message, User> = Property.newProperty()

    val DEFAULT_MESSAGE: Instantiator<Message> = Instantiator { lookup ->
        Message(
                message = lookup.valueOf(MESSAGE, "dummy text of the message"),
                sender = lookup.valueOf(SENDER, make(a(DEFAULT_USER, with(NICKNAME, "dummy_sender"), with(ID, "sender_id")))),
                receiver = lookup.valueOf(RECEIVER, make(a(DEFAULT_USER, with(NICKNAME, "dummy_receiver"), with(ID, "receiver_id"))))
        )
    }
}