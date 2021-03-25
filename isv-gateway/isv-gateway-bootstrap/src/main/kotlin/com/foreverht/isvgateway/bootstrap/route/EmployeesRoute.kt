package com.foreverht.isvgateway.bootstrap.route

import com.foreverht.isvgateway.bootstrap.handler.ISVAccessTokenAuthorizationHandler
import com.foreverht.isvgateway.bootstrap.validation.EmployeeValidationHandler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EmployeesRoute(vertx: Vertx, router: Router):AbstractISVRoute(vertx = vertx,router = router) {

    init {
        queryEmployeeByIdRoute()
        batchQueryEmployeeRoute()
        searchEmployeesRoute()
    }

    private fun searchEmployeesRoute(){
        createGetRoute(path = "/$version/organizations/:orgCode/employeesSearch"){ route ->

            route.handler(EmployeeValidationHandler().searchEmployeesValidationHandler())
            route.handler(ISVAccessTokenAuthorizationHandler(vertx))

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val accessToken = it.get<String>("accessToken")
                        val employeeApplication = getEmployeeApplication(accessToken = accessToken).await()

                        var orgCode = it.pathParam("orgCode")
                        val query = it.queryParam("query")[0]

                        val employeeList = employeeApplication.searchEmployees(isvAccessToken = accessToken,orgCode = orgCode,query = query).await()
                        it.end(JsonArray(employeeList.map(JsonObject::mapFrom)).toBuffer())
                    }catch (t:Throwable){
                        it.fail(t)
                    }
                }
            }

        }
    }

    private fun batchQueryEmployeeRoute(){
        createGetRoute(path = "/$version/organizations/:orgCode/employeesBatch"){ route ->

            route.handler(EmployeeValidationHandler().queryBatchQueryEmployeeValidationHandler())
            route.handler(ISVAccessTokenAuthorizationHandler(vertx))

            route.handler{
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val accessToken = it.get<String>("accessToken")
                        val employeeApplication = getEmployeeApplication(accessToken = accessToken).await()

                        var orgCode = it.pathParam("orgCode")
                        val userIds = it.queryParam("userIds")[0]

                        val employeeList = employeeApplication.batchQueryEmployeeByIds(isvAccessToken = accessToken,orgCode = orgCode,userIdList = userIds.split(",")).await()
                        it.end(JsonArray(employeeList.map(JsonObject::mapFrom)).toBuffer())
                    }catch (t:Throwable){
                        it.fail(t)
                    }
                }
            }

        }
    }

    private fun queryEmployeeByIdRoute(){
        createGetRoute(path = "/$version/organizations/:orgCode/employees/:employeeId"){ route ->

            route.handler(EmployeeValidationHandler().queryEmployeeByIdValidationHandler())
            route.handler(ISVAccessTokenAuthorizationHandler(vertx))

            route.handler{
                GlobalScope.launch(vertx.dispatcher()) {
                    try {
                        val accessToken = it.get<String>("accessToken")
                        val employeeApplication = getEmployeeApplication(accessToken = accessToken).await()

                        val employeeId = it.pathParam("employeeId")
                        var orgCode = it.pathParam("orgCode")

                        val employeeDTO = employeeApplication.queryEmployeeById(isvAccessToken = accessToken,orgCode = orgCode,userId = employeeId).await()
                        it.end(JsonObject.mapFrom(employeeDTO).toBuffer())
                    }catch (t:Throwable){
                        it.fail(t)
                    }
                }
            }

        }
    }
}