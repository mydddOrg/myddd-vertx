package com.foreverht.isvgateway.bootstrap.route

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.foreverht.isvgateway.api.ISVClientApplication
import com.foreverht.isvgateway.api.dto.ISVClientDTO
import com.foreverht.isvgateway.bootstrap.ISVClientErrorCode
import com.foreverht.isvgateway.bootstrap.validation.ISVClientValidationHandler
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.oauth2.api.OAuth2Application
import org.myddd.vertx.oauth2.api.OAuth2ClientApplication
import org.myddd.vertx.web.router.AbstractRouter
import java.util.*

class ISVClientRouter(vertx: Vertx,router: Router) : AbstractRouter(vertx = vertx,router = router) {

    private val oAuth2Application by lazy { InstanceFactory.getInstance(OAuth2Application::class.java) }
    private val oAuth2ClientApplication by lazy {InstanceFactory.getInstance(OAuth2ClientApplication::class.java)}

    private val isvClientApplication by lazy { InstanceFactory.getInstance(ISVClientApplication::class.java) }

    companion object {
        private val logger by lazy { LoggerFactory.getLogger(ISVClientRouter::class.java) }
    }

    init {
        createISVClientRoute()
        queryISVClientRoute()
        updateISVClientRoute()
        requestClientTokenRoute()
        refreshClientTokenToken()
        revokeClientTokenRoute()
        resetClientSecretRoute()
    }

    private fun createISVClientRoute(){
        createPostRoute("/$version/clients"){ route ->

            route.handler(ISVClientValidationHandler().createISVClientValidation())

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val bodyString = it.bodyAsString
                        val mapper = ObjectMapper().registerModule(KotlinModule())
                        val isvClientDTO = mapper.readValue(bodyString,ISVClientDTO::class.java)

                        val created = isvClientApplication.createISVClient(isvClientDTO).await()
                        it.end(JsonObject.mapFrom(created).toBuffer())
                    }catch (t:Throwable){
                        logger.error(t)
                        it.fail(HTTP_400_RESPONSE, t)
                    }
                }
            }
        }
    }


    private fun queryISVClientRoute(){
        createGetRoute("/$version/clients/:clientId"){ route ->
            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val clientId = it.pathParam("clientId")
                        val queryClient = isvClientApplication.queryClientByClientId(clientId).await()
                        if(Objects.nonNull(queryClient)){
                            it.end(JsonObject.mapFrom(queryClient).toBuffer())
                        }else{
                            throw BusinessLogicException(ISVClientErrorCode.CLIENT_NOT_EXISTS)
                        }
                    }catch (t:Throwable){
                        logger.error(t)
                        it.fail(HTTP_400_RESPONSE, t)
                    }
                }
            }
        }
    }

    private fun updateISVClientRoute(){
        createPatchRoute("/$version/clients/:clientId"){ route ->

            route.handler(ISVClientValidationHandler().updateISVClientValidation())

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val bodyString = it.bodyAsString
                        val mapper = ObjectMapper().registerModule(KotlinModule())
                        val isvClientDTO = mapper.readValue(bodyString,ISVClientDTO::class.java)

                        val created = isvClientApplication.updateISVClient(isvClientDTO).await()
                        it.end(JsonObject.mapFrom(created).toBuffer())
                    }catch (t:Throwable){
                        logger.error(t)
                        it.fail(HTTP_400_RESPONSE,t)
                    }
                }
            }
        }
    }

    private fun requestClientTokenRoute(){
        createPostRoute("/$version/clients/token"){ route ->

            route.handler(ISVClientValidationHandler().requestAccessTokenValidation())

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val jsonBody = it.bodyAsJson
                        val clientId = jsonBody.getString("clientId")
                        val clientSecret = jsonBody.getString("clientSecret")
                        val userDTO = oAuth2Application.requestClientToken(clientId,clientSecret).await()
                        val requestToken = JsonObject.mapFrom(userDTO?.tokenDTO)
                        it.end(requestToken.toBuffer())
                    }catch (t:Throwable){
                        logger.error(t)
                        it.fail(HTTP_400_RESPONSE,t)
                    }
                }
            }
        }
    }

    private fun refreshClientTokenToken(){
        createPostRoute("/$version/clients/refreshToken"){ route ->
            route.handler(ISVClientValidationHandler().refreshTokenValidation())
            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val bodyJson = it.bodyAsJson
                        val clientId = bodyJson.getString("clientId")
                        val refreshToken = bodyJson.getString("refreshToken")

                        val tokenDTO = oAuth2Application.refreshUserToken(clientId,refreshToken).await()
                        val requestToken = JsonObject.mapFrom(tokenDTO?.tokenDTO)

                        it.end(requestToken.toBuffer())

                    }catch (t:Throwable){
                        logger.error(t)
                        it.fail(HTTP_400_RESPONSE,t)
                    }
                }
            }
        }
    }

    private fun revokeClientTokenRoute(){
        createDeleteRoute("/$version/clients/:clientId/token/:accessToken"){ route ->
            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val clientId = it.pathParam("clientId")
                        val accessToken = it.pathParam("accessToken")

                        oAuth2Application.revokeUserToken(clientId,accessToken).await()
                        it.response().setStatusCode(204).end()

                    }catch (t:Throwable){
                        logger.error(t)
                        it.fail(HTTP_400_RESPONSE,t)
                    }
                }
            }
        }
    }

    private fun resetClientSecretRoute(){
        createPatchRoute("/$version/clients/:clientId/clientSecret"){ route ->
            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try{
                        val clientId = it.pathParam("clientId")
                        val body = it.bodyAsJson
                        val resetSecret = oAuth2ClientApplication.resetClientSecret(clientId).await()

                        it.end(JsonObject().put("clientSecret",resetSecret).toBuffer())

                    }catch (e:Exception){
                        it.fail(HTTP_400_RESPONSE,e)
                    }
                }
            }
        }
    }



}