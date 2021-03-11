package com.foreverht.isvgateway.bootstrap.route

import com.foreverht.isvgateway.api.OrganizationApplication
import com.foreverht.isvgateway.bootstrap.validation.OrganizationValidationHandler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.web.router.handler.AccessTokenAuthorizationHandler

class OrganizationRouter(vertx: Vertx,router: Router):AbstractISVRouter(vertx = vertx,router = router) {

    init {
        queryOrganizationRoute()
    }

    private fun queryOrganizationRoute(){
        createGetRoute(path = "/$version/organizations",validationHandler = OrganizationValidationHandler().queryOrganizationValidation()){ route ->

            route.handler(AccessTokenAuthorizationHandler(vertx))

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val orgList = it.queryParam("orgId")
                        val orgId = if(orgList.isNotEmpty()) orgList[0] else null

                        val orgCode = it.queryParam("orgCode")?.get(0)!!
                        val clientId = it.get<String>("clientId")
                        val accessToken = it.get<String>("accessToken")

                        val organizationApplication = getOrganizationApplication(accessToken = accessToken).await()

                        val organizationDTO = organizationApplication.queryOrganizationById(clientId = clientId,orgCode = orgCode,orgId = orgId).await()
                        it.end(JsonObject.mapFrom(organizationDTO).toBuffer())
                    }catch (t:Throwable){
                        it.fail(t)
                    }

                }
            }
        }
    }
}