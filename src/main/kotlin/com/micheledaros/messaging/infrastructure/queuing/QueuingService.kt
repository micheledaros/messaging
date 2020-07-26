package com.micheledaros.messaging.infrastructure.queuing

import com.micheledaros.messaging.message.domain.Message

interface QueuingService {
    fun sendMessage(message: Message)
}