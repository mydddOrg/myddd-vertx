package org.myddd.vertx.oauth2.provider

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.User
import io.vertx.ext.auth.oauth2.OAuth2Auth
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.oauth2.api.OAuth2Application
import org.myddd.vertx.oauth2.api.OAuth2UserDTO
import java.util.*

class MydddVertXOAuth2Provider : AbstractOAuth2Auth() {

    private val vertx: Vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }
    private val databaseOAuth2Application by lazy { InstanceFactory.getInstance(OAuth2Application::class.java) }

    companion object {
        const val CLIENT_ID = "clientId"
        const val CLIENT_SECRET = "clientSecret"
    }
    override fun authenticate(credentials: JsonObject?, resultHandler: Handler<AsyncResult<User>>?) {
        check(Objects.nonNull(credentials?.getString(CLIENT_ID)) && Objects.nonNull(credentials?.getString(CLIENT_SECRET))){
            "请指定client id以及client secret"
        }
        GlobalScope.launch(vertx.dispatcher()) {
            databaseOAuth2Application.requestClientToken(credentials!!.getString(CLIENT_ID),credentials.getString(CLIENT_SECRET))
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

        GlobalScope.launch(vertx.dispatcher()) {
            databaseOAuth2Application.refreshUserToken(auth2User.clientId, auth2User.tokenDTO!!.refreshToken).onSuccess {
                handler?.handle(Future.succeededFuture(it))
            }.onFailure {
                handler?.handle(Future.failedFuture(it))
            }
        }
        return this
    }

    override fun revoke(user: User?, tokenType: String?, handler: Handler<AsyncResult<Void>>?): OAuth2Auth {
        check(Objects.nonNull(user) && Objects.nonNull((user as OAuth2UserDTO).clientId) && Objects.nonNull((user as OAuth2UserDTO).tokenDTO)){
            "CLIENT_ID_NULL"
        }
        GlobalScope.launch(vertx.dispatcher()) {
            val auth2User = user as OAuth2UserDTO

            databaseOAuth2Application.revokeUserToken(auth2User.clientId,auth2User.tokenDTO!!.accessToken).onSuccess {
                handler?.handle(Future.succeededFuture())
            }.onFailure {
                handler?.handle(Future.failedFuture(it))
            }
        }
        return this
    }


}