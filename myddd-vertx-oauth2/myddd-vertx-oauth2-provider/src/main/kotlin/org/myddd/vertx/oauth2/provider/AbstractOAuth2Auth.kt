package org.myddd.vertx.oauth2.provider

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.User
import io.vertx.ext.auth.oauth2.AccessToken
import io.vertx.ext.auth.oauth2.OAuth2Auth
import io.vertx.ext.auth.oauth2.OAuth2FlowType
import io.vertx.ext.auth.oauth2.OAuth2RBAC

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

    override fun decodeToken(token: String?, handler: Handler<AsyncResult<AccessToken>>?): OAuth2Auth {
        throw UnsupportedOperationException()
    }

    override fun introspectToken(
        token: String?,
        tokenType: String?,
        handler: Handler<AsyncResult<AccessToken>>?
    ): OAuth2Auth {
        throw UnsupportedOperationException()
    }

    override fun getFlowType(): OAuth2FlowType {
        throw UnsupportedOperationException()
    }

    override fun rbacHandler(rbac: OAuth2RBAC?): OAuth2Auth {
        throw UnsupportedOperationException()
    }
}