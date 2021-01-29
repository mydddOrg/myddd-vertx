package org.myddd.vertx.oauth2.provider

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.impl.future.PromiseImpl
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.User
import io.vertx.ext.auth.oauth2.OAuth2Auth
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.oauth2.api.DatabaseOAuth2Application
import org.myddd.vertx.oauth2.api.OAuth2UserDTO
import java.lang.RuntimeException
import java.util.*

class MydddVertXOAuth2Provider : AbstractOAuth2Auth() {

    private val databaseOAuth2Application by lazy { InstanceFactory.getInstance(DatabaseOAuth2Application::class.java) }

    override fun authenticate(credentials: JsonObject?, resultHandler: Handler<AsyncResult<User>>?) {
        require(Objects.nonNull(credentials)){
            "请指定client id以及client secret"
        }
        GlobalScope.launch {
            databaseOAuth2Application.validateClientUser(credentials!!.getString("clientId"),credentials!!.getString("clientSecret"))
                .onSuccess {
                    resultHandler?.handle(Future.succeededFuture(it))
                }.onFailure {
                    resultHandler?.handle(Future.failedFuture(it))
                }
        }
    }

    override fun refresh(user: User?, handler: Handler<AsyncResult<User>>?): OAuth2Auth {
        require(Objects.nonNull(user)){
            RuntimeException("USER_NULL")
        }

        val auth2User = user as OAuth2UserDTO
        require(Objects.nonNull(auth2User.tokenDTO)){
            RuntimeException("USER_TOKEN_NULL")
        }

        GlobalScope.launch {
            databaseOAuth2Application.refreshUserToken(auth2User.clientId, auth2User!!.tokenDTO!!.refreshToken).onSuccess {
                handler?.handle(Future.succeededFuture(it))
            }.onFailure {
                handler?.handle(Future.failedFuture(it))
            }
        }
        return this
    }

    override fun revoke(user: User?, tokenType: String?, handler: Handler<AsyncResult<Void>>?): OAuth2Auth {
        if(Objects.nonNull(user)){
            val auth2User = user as OAuth2UserDTO
            GlobalScope.launch {
                databaseOAuth2Application.revokeUserToken(auth2User.clientId).onSuccess {
                    handler?.handle(Future.succeededFuture())
                }.onFailure {
                    handler?.handle(Future.failedFuture(it))
                }
            }
        }
        handler?.handle(Future.succeededFuture())
        return this
    }
}