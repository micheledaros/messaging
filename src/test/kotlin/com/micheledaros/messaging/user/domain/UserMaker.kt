package com.micheledaros.messaging.user.domain

import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property

object UserMaker {
    val ID: Property<User, String> = Property.newProperty()
    val NICKNAME: Property<User, String> = Property.newProperty()

    val DEFAULT_USER: Instantiator<User> = Instantiator { lookup ->
        User(
                id = lookup.valueOf(ID, "01234567890123456789012345678901"),
                nickName = lookup.valueOf(NICKNAME, "dummy_test_nickname")
        )
    }
}