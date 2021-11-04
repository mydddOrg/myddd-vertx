package org.myddd.vertx.oauth2.api

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.AuthProvider
import io.vertx.ext.auth.User
import io.vertx.ext.auth.authorization.Authorization
import java.util.*

class OAuth2UserDTO : User {

    lateinit var clientId:String

    lateinit var clientName:String

    var tokenDTO: OAuth2TokenDTO? = null

    override fun expired(): Boolean {
        return Objects.isNull(tokenDTO) || tokenDTO!!.accessExpiredIn < System.currentTimeMillis()
    }

    override fun attributes(): JsonObject {
        throw UnsupportedOperationException()
    }

    override fun isAuthorized(authority: Authorization?, resultHandler: Handler<AsyncResult<Boolean>>?): User {
        throw UnsupportedOperationException()
    }

    override fun principal(): JsonObject {
        throw UnsupportedOperationException()
    }

    override fun setAuthProvider(authProvider: AuthProvider?) {
        throw UnsupportedOperationException()
    }

    override fun merge(other: User?): User {
        return this
    }
}