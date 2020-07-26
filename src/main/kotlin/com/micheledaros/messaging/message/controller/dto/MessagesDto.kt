package com.micheledaros.messaging.message.controller.dto

data class MessagesDto (
        val messages: List<MessageDto>,
        val hasMore: Boolean
)