package org.myddd.vertx.oauth2.start.router

import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.domain.BusinessLogicException
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.oauth2.api.OAuth2ClientApplication
import org.myddd.vertx.oauth2.api.OAuth2ClientDTO
import org.myddd.vertx.oauth2.start.OAuth2WebErrorCode
import kotlin.Exception

class OAuth2ClientRouter constructor(router:Router,vertx:Vertx) : AbstractOAuth2Router(router,vertx) {

    private val oAuth2ClientApplication:OAuth2ClientApplication by lazy { InstanceFactory.getInstance(OAuth2ClientApplication::class.java) }

    private val basePath = "oauth2"

    init {
        createClientRoute()
        resetClientSecretRoute()
        disabledStatusClientRoute()
    }

    private fun createClientRoute(){
        createRoute(HttpMethod.POST,"/$version/$basePath/clients"){
            GlobalScope.launch(vertx.dispatcher()) {
                try {
                    val body = it.bodyAsJson
                    if(body.getString("clientId").isNullOrEmpty() || body.getString("name").isNullOrEmpty()){
                        throw BusinessLogicException(OAuth2WebErrorCode.ILLEGAL_PARAMETER_FOR_CREATE_CLIENT)
                    }

                    val createClientDTO = body.mapTo(OAuth2ClientDTO::class.java)

                    val created = oAuth2ClientApplication.createClient(createClientDTO).await()
                    val createdJson = JsonObject.mapFrom(created)
                    it.end(createdJson.toBuffer())
                } catch (e: Exception) {
                    it.fail(HTTP_400_RESPONSE, e)
                }
            }
        }
    }

    private fun resetClientSecretRoute(){
        createRoute(HttpMethod.PATCH,"/$version/$basePath/clients/:clientId/clientSecret"){
            GlobalScope.launch(vertx.dispatcher()) {
                try{
                    val clientId = it.pathParam("clientId")
                    val body = it.bodyAsJson
                    val clientSecret = body.getString("clientSecret")

                    if(clientId.isNullOrEmpty() || clientSecret.isNullOrEmpty()){
                        throw BusinessLogicException(OAuth2WebErrorCode.ILLEGAL_PARAMETER_FOR_CLIENT_ID_AND_CLIENT_SECRET)
                    }

                    val oauth2Client = oAuth2ClientApplication.queryClient(clientId).await()
                        ?: throw BusinessLogicException(OAuth2WebErrorCode.CLIENT_NOT_FOUND)

                    if(clientSecret != oauth2Client.clientSecret){
                        throw BusinessLogicException(OAuth2WebErrorCode.CLIENT_SECRET_NOT_MATCH)
                    }

                    val resetSecret = oAuth2ClientApplication.resetClientSecret(clientId).await()

                    it.end(JsonObject().put("clientSecret",resetSecret).toBuffer())

                }catch (e:Exception){
                    it.fail(HTTP_400_RESPONSE,e)
                }
            }
        }
    }

    private fun disabledStatusClientRoute(){
        createRoute(HttpMethod.PATCH,"/$version/$basePath/clients/:clientId/disabledStatus"){
            GlobalScope.launch(vertx.dispatcher()) {
                try {
                    val clientId = it.pathParam("clientId")
                    val jsonBody = it.bodyAsJson
                    val clientSecret = jsonBody.getString("clientSecret")

                    if(clientId.isNullOrEmpty() || clientSecret.isNullOrEmpty()){
                        throw BusinessLogicException(OAuth2WebErrorCode.ILLEGAL_PARAMETER_FOR_CLIENT_ID_AND_CLIENT_SECRET)
                    }

                    val oauth2Client = oAuth2ClientApplication.queryClient(clientId).await()
                        ?: throw BusinessLogicException(OAuth2WebErrorCode.CLIENT_NOT_FOUND)

                    if(clientSecret != oauth2Client.clientSecret){
                        throw BusinessLogicException(OAuth2WebErrorCode.CLIENT_SECRET_NOT_MATCH)
                    }

                    oAuth2ClientApplication.disableClient(clientId).await()

                    it.response().setStatusCode(204).end()

                }catch (e:Exception){
                    it.fail(400,e)
                }
            }
        }
    }

}