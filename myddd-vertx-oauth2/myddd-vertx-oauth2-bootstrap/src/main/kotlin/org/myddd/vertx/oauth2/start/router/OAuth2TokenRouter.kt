package org.myddd.vertx.oauth2.start.router

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.oauth2.api.OAuth2Application
import org.myddd.vertx.oauth2.start.OAuth2WebErrorCode
import org.myddd.vertx.web.router.AbstractRouter
import kotlin.Exception

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

                        if(grantType != "client_credentials") throw BusinessLogicException(OAuth2WebErrorCode.NOT_SUPPORT_OAUTH2_GRANT_TYPE)

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

                        if(clientId.isNullOrEmpty() || refreshToken.isNullOrEmpty()) throw BusinessLogicException(OAuth2WebErrorCode.ILLEGAL_PARAMETER_FOR_REFRESH_TOKEN)

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

                        if(clientId.isNullOrEmpty() || accessToken.isNullOrEmpty()) throw BusinessLogicException(OAuth2WebErrorCode.ILLEGAL_PARAMETER_FOR_REVOKE_TOKEN)

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