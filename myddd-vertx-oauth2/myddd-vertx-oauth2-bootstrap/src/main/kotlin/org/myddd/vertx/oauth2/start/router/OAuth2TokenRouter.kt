package org.myddd.vertx.oauth2.start.router

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.oauth2.api.OAuth2Application
import org.myddd.vertx.oauth2.start.NotSupportOAuth2GrantTypeException
import org.myddd.vertx.web.router.AbstractRouter

class OAuth2TokenRouter(vertx: Vertx,router: Router) : AbstractRouter(vertx = vertx, router = router) {

    private val basePath = "oauth2"

    private val oAuth2Application by lazy { InstanceFactory.getInstance(OAuth2Application::class.java) }

    init {
        requestClientTokenRoute()
        refreshClientTokenToken()
        revokeClientTokenRoute()
    }

    private fun requestClientTokenRoute(){
        createPostRoute("/$version/$basePath/token"){ route ->
            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val jsonBody = it.bodyAsJson
                        val grantType = jsonBody.getString("grantType")
                        val clientId = jsonBody.getString("clientId")
                        val clientSecret = jsonBody.getString("clientSecret")

                        if(grantType != "client_credentials") throw NotSupportOAuth2GrantTypeException()

                        val userDTO = oAuth2Application.requestClientToken(clientId,clientSecret).await()

                        val requestToken = JsonObject.mapFrom(userDTO?.tokenDTO)

                        it.end(requestToken.toBuffer())

                    }catch (t:Throwable){
                        it.fail(HTTP_400_RESPONSE,t)
                    }
                }
            }
        }
    }

    private fun refreshClientTokenToken(){
        createPostRoute("/$version/$basePath/refreshToken"){ route ->
            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val bodyJson = it.bodyAsJson
                        val clientId = bodyJson.getString("clientId")
                        val refreshToken = bodyJson.getString("refreshToken")

                        if(clientId.isNullOrEmpty() || refreshToken.isNullOrEmpty())
                            throw IllegalArgumentException("clientId与refreshToken不能为空")

                        val tokenDTO = oAuth2Application.refreshUserToken(clientId,refreshToken).await()
                        val requestToken = JsonObject.mapFrom(tokenDTO?.tokenDTO)

                        it.end(requestToken.toBuffer())

                    }catch (t:Throwable){
                        it.fail(HTTP_400_RESPONSE,t)
                    }
                }
            }
        }
    }

    private fun revokeClientTokenRoute(){
        createDeleteRoute("/$version/$basePath/clients/:clientId/token/:accessToken"){ route ->
            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val clientId = it.pathParam("clientId")
                        val accessToken = it.pathParam("accessToken")

                        if(clientId.isNullOrEmpty() || accessToken.isNullOrEmpty())
                            throw IllegalArgumentException("clientId与accessToken不能为空")

                        oAuth2Application.revokeUserToken(clientId,accessToken).await()
                        it.response().setStatusCode(204).end()

                    }catch (t:Throwable){
                        it.fail(HTTP_400_RESPONSE,t)
                    }
                }
            }
        }
    }
}