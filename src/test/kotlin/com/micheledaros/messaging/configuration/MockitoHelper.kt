package com.micheledaros.messaging.configuration

import org.mockito.Mockito

// https://stackoverflow.com/questions/59230041/argumentmatchers-any-must-not-be-null
fun <T> anyObject(): T = Mockito.any<T>()

