package com.micheledaros.messaging.infrastructure

import java.util.UUID

fun generateId() : String = UUID.randomUUID().toString().replace("-","")