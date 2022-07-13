package org.myddd.vertx.oauth2.provider

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.User
import io.vertx.ext.auth.oauth2.OAuth2Auth

abstract class AbstractOAuth2Auth : OAuth2Auth {

    override fun jWKSet(handler: Handler<AsyncResult<Void>>?): OAuth2Auth {
        throw UnsupportedOperationException()
    }

    override fun missingKeyHandler(handler: Handler<String>?): OAuth2Auth {
        throw UnsupportedOperationException()
    }

    override fun authorizeURL(params: JsonObject?): String {
        throw UnsupportedOperationException()
    }

    override fun userInfo(user: User?, handler: Handler<AsyncResult<JsonObject>>?): OAuth2Auth {
        throw UnsupportedOperationException()
    }

    override fun endSessionURL(user: User?, params: JsonObject?): String {
        throw UnsupportedOperationException()
    }

    override fun close() {
//        do nothing here
    }

    override fun jWKSet(): Future<Void> {
        throw UnsupportedOperationException()
    }

    override fun userInfo(user: User?): Future<JsonObject> {
        throw UnsupportedOperationException()
    }

    override fun revoke(user: User?, tokenType: String?): Future<Void> {
        throw UnsupportedOperationException()
    }
}