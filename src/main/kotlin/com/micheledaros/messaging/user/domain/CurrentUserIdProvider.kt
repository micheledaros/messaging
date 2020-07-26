package com.micheledaros.messaging.user.domain

import org.springframework.stereotype.Service
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

interface CurrentUserIdProvider {
    fun get() : String?
}

@Service
class CurrentRestUserIdProvider : CurrentUserIdProvider {

    companion object {
        const val USER_ID_HEADER = "userid"
    }

    override fun get() : String?{
        return (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)
                ?.request
                ?.getHeader(USER_ID_HEADER)
    }

}