package org.myddd.vertx.oauth2.start.router

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.oauth2.ClientNotFoundException
import org.myddd.vertx.oauth2.ClientSecretNotMatchException
import org.myddd.vertx.oauth2.api.OAuth2ClientApplication
import org.myddd.vertx.oauth2.api.OAuth2ClientDTO
import org.myddd.vertx.web.router.AbstractRouter

class OAuth2ClientRouter constructor(router:Router,vertx:Vertx,coroutineScope: CoroutineScope) : AbstractRouter(vertx = vertx,router = router,coroutineScope = coroutineScope) {

    private val oAuth2ClientApplication:OAuth2ClientApplication by lazy { InstanceFactory.getInstance(OAuth2ClientApplication::class.java) }

    private val basePath = "oauth2"

    init {
        createClientRoute()
        resetClientSecretRoute()
        disabledStatusClientRoute()
    }

    private fun createClientRoute(){
        createPostRoute("/$version/$basePath/clients"){ route ->
            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val body = it.bodyAsJson
                        if(body.getString("clientId").isNullOrEmpty() || body.getString("name").isNullOrEmpty())throw IllegalArgumentException("clientId与name不能为空")

                        val createClientDTO = body.mapTo(OAuth2ClientDTO::class.java)

                        val created = oAuth2ClientApplication.createClient(createClientDTO).await()
                        val createdJson = JsonObject.mapFrom(created)
                        it.end(createdJson.toBuffer())
                    } catch (t: Throwable) {
                        it.fail(HTTP_400_RESPONSE, t)
                    }
                }
            }
        }
    }

    private fun resetClientSecretRoute(){
        createPatchRoute("/$version/$basePath/clients/:clientId/clientSecret"){ route ->
            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try{
                        val clientId = it.pathParam("clientId")
                        val body = it.bodyAsJson
                        val clientSecret = body.getString("clientSecret")

                        if(clientId.isNullOrEmpty() || clientSecret.isNullOrEmpty()){
                            throw IllegalArgumentException("clientId与clientSecret不能为空")
                        }

                        val oauth2Client = oAuth2ClientApplication.queryClient(clientId).await()
                            ?: throw ClientNotFoundException()

                        if(clientSecret != oauth2Client.clientSecret){
                            throw ClientSecretNotMatchException()
                        }

                        val resetSecret = oAuth2ClientApplication.resetClientSecret(clientId).await()

                        it.end(JsonObject().put("clientSecret",resetSecret).toBuffer())

                    }catch (t:Throwable){
                        it.fail(HTTP_400_RESPONSE,t)
                    }
                }
            }
        }
    }

    private fun disabledStatusClientRoute(){
        createPatchRoute("/$version/$basePath/clients/:clientId/disabledStatus"){ route ->
            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val clientId = it.pathParam("clientId")
                        val jsonBody = it.bodyAsJson
                        val clientSecret = jsonBody.getString("clientSecret")

                        if(clientId.isNullOrEmpty() || clientSecret.isNullOrEmpty())throw IllegalArgumentException("clientId以及clientSecret不能为空")

                        val oauth2Client = oAuth2ClientApplication.queryClient(clientId).await()
                            ?: throw ClientNotFoundException()

                        if(clientSecret != oauth2Client.clientSecret){
                            throw ClientSecretNotMatchException()
                        }

                        oAuth2ClientApplication.disableClient(clientId).await()

                        it.response().setStatusCode(204).end()

                    }catch (t:Throwable){
                        it.fail(HTTP_400_RESPONSE,t)
                    }
                }
            }
        }
    }

}