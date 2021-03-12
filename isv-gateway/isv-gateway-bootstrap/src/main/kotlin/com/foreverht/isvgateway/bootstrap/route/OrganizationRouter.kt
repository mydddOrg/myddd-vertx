package com.foreverht.isvgateway.bootstrap.route

import com.foreverht.isvgateway.api.dto.OrganizationDTO
import com.foreverht.isvgateway.bootstrap.validation.OrganizationValidationHandler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
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
        queryOrganizationChildrenRoute()
        queryOrganizationEmployeeRoute()
    }

    private fun queryOrganizationEmployeeRoute(){

        createGetRoute(path = "/$version/organizations/:orgCode/employees"){ route ->

            route.handler(OrganizationValidationHandler().queryOrganizationValidation())

            route.handler(AccessTokenAuthorizationHandler(vertx))

            route.handler {

                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val clientId = it.get<String>("clientId")
                        val accessToken = it.get<String>("accessToken")
                        val orgCode = it.pathParam("orgCode")

                        val orgId = if(it.queryParam("orgId").isNotEmpty()) it.queryParam("orgId")[0] else null
                        val limit = if(it.queryParam("limit").isNotEmpty()) it.queryParam("limit")[0].toInt() else 50
                        val skip = if(it.queryParam("skip").isNotEmpty()) it.queryParam("skip")[0].toInt() else 0

                        val organizationApplication = getOrganizationApplication(accessToken = accessToken).await()

                        val employeeList =  organizationApplication.queryOrganizationEmployees(clientId = clientId,orgCode = orgCode,orgId = orgId,limit = limit,skip = skip).await()

                        it.end(JsonArray(employeeList.map(JsonObject::mapFrom)).toBuffer())

                        it.end(JsonArray(employeeList.map(JsonObject::mapFrom)).toBuffer())
                    }catch (t:Throwable){
                        it.fail(t)
                    }
                }

            }
        }
    }


    private fun queryOrganizationChildrenRoute(){
        createGetRoute(path = "/$version/organizations/:orgCode/children"){ route ->

            route.handler(OrganizationValidationHandler().queryOrganizationChildrenOrEmployeeValidation())

            route.handler(AccessTokenAuthorizationHandler(vertx))

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val clientId = it.get<String>("clientId")
                        val accessToken = it.get<String>("accessToken")
                        val orgCode = it.pathParam("orgCode")

                        val orgId = if(it.queryParam("orgId").isNotEmpty()) it.queryParam("orgId")[0] else null
                        val limit = if(it.queryParam("limit").isNotEmpty()) it.queryParam("limit")[0].toInt() else 50
                        val skip = if(it.queryParam("skip").isNotEmpty()) it.queryParam("skip")[0].toInt() else 0

                        val organizationApplication = getOrganizationApplication(accessToken = accessToken).await()

                        val organizationList: List<OrganizationDTO> =  organizationApplication.queryChildrenOrganizations(clientId = clientId,orgCode = orgCode,orgId = orgId,limit = limit,skip = skip).await()

                        it.end(JsonArray(organizationList.map(JsonObject::mapFrom)).toBuffer())
                    }catch (t:Throwable){
                        it.fail(t)
                    }
                }
            }

        }
    }

    private fun queryOrganizationRoute(){
        createGetRoute(path = "/$version/organizations/:orgCode"){ route ->

            route.handler(OrganizationValidationHandler().queryOrganizationValidation())

            route.handler(AccessTokenAuthorizationHandler(vertx))

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val orgList = it.queryParam("orgId")
                        val orgId = if(orgList.isNotEmpty()) orgList[0] else null

                        val orgCode = it.pathParam("orgCode")
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