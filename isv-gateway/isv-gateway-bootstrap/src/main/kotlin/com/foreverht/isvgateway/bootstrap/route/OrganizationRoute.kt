package com.foreverht.isvgateway.bootstrap.route


import com.foreverht.isvgateway.api.OrganizationApplication
import com.foreverht.isvgateway.api.dto.OrgPageQueryDTO
import com.foreverht.isvgateway.api.dto.OrganizationDTO
import com.foreverht.isvgateway.bootstrap.handler.ISVAccessTokenAuthorizationHandler
import com.foreverht.isvgateway.bootstrap.validation.OrganizationValidationHandler
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.web.router.ext.singleQueryParam

class OrganizationRoute(vertx: Vertx, router: Router):AbstractISVRoute(vertx = vertx,router = router) {

    init {
        queryOrganizationRoute()
        queryOrganizationChildrenRoute()
        queryOrganizationEmployeeRoute()
    }

    private fun queryOrganizationEmployeeRoute(){

        createGetRoute(path = "/$version/organizations/:orgCode/employees"){ route ->

            route.handler(OrganizationValidationHandler().queryOrganizationValidation())

            route.handler(ISVAccessTokenAuthorizationHandler(vertx))

            route.handler {

                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val (orgPageQueryDTO,organizationApplication) = parsePageQueryParam(it).await()
                        val employeeList = organizationApplication.queryOrganizationEmployees(orgPageQueryDTO).await()
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

            route.handler(ISVAccessTokenAuthorizationHandler(vertx))

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val (orgPageQueryDTO,organizationApplication) = parsePageQueryParam(it).await()
                        val organizationList: List<OrganizationDTO> =  organizationApplication.queryChildrenOrganizations(orgPageQueryDTO).await()
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

            route.handler(ISVAccessTokenAuthorizationHandler(vertx))

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val orgList = it.queryParam("orgId")
                        val orgId = if(orgList.isNotEmpty()) orgList[0] else null

                        val orgCode = it.pathParam("orgCode")
                        val accessToken = it.get<String>("accessToken")

                        val organizationApplication = getOrganizationApplication(accessToken = accessToken).await()

                        val organizationDTO = organizationApplication.queryOrganizationById(isvAccessToken = accessToken,orgCode = orgCode,orgId = orgId).await()
                        it.end(JsonObject.mapFrom(organizationDTO).toBuffer())
                    }catch (t:Throwable){
                        it.fail(t)
                    }
                }
            }

        }
    }

    private suspend fun parsePageQueryParam(it:RoutingContext): Future<Pair<OrgPageQueryDTO,OrganizationApplication>> {
        return try {
            val accessToken = it.get<String>("accessToken")
            val orgCode = it.pathParam("orgCode")

            val orgId = it.singleQueryParam("orgId")
            val limit = it.singleQueryParam("limit","50")!!.toInt()
            val skip = it.singleQueryParam("skip","0")!!.toInt()

            val organizationApplication = getOrganizationApplication(accessToken = accessToken).await()

            Future.succeededFuture(Pair(OrgPageQueryDTO(accessToken = accessToken,orgCode = orgCode,orgId = orgId,limit = limit,skip = skip),organizationApplication))
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}