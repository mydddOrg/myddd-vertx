package org.myddd.vertx.oauth2.provider

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.User
import io.vertx.ext.auth.oauth2.OAuth2Auth

class MydddVertXOAuth2Provider : AbstractOAuth2Auth() {

    override fun authenticate(credentials: JsonObject?, resultHandler: Handler<AsyncResult<User>>?) {
        TODO("Not yet implemented")
    }

    override fun refresh(user: User?, handler: Handler<AsyncResult<User>>?): OAuth2Auth {
        TODO("Not yet implemented")
    }

    override fun revoke(user: User?, tokenType: String?, handler: Handler<AsyncResult<Void>>?): OAuth2Auth {
        TODO("Not yet implemented")
    }
}