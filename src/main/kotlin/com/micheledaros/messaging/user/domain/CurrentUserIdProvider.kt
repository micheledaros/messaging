package com.micheledaros.messaging.user.domain

import org.springframework.stereotype.Service
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

interface CurrentUserIdProvider {
    fun get() : String?
}

@Service
class CurrentRestUserIdProvider : CurrentUserIdProvider {

    override fun get() : String?{
        return (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)
                ?.request
                ?.getHeader("userid")
    }

}