package com.foreverht.isvgateway.bootstrap.route

import com.foreverht.isvgateway.api.ISVAuthCodeApplication
import com.foreverht.isvgateway.api.ISVSuiteTicketApplication
import com.foreverht.isvgateway.bootstrap.ext.jsonFormatEnd
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.ioc.InstanceFactory

class MockRoute(vertx: Vertx, router: Router):AbstractISVRoute(vertx = vertx,router = router) {

    private val isvSuiteTicketApplication by lazy { InstanceFactory.getInstance(ISVSuiteTicketApplication::class.java) }
    private val isvAuthCodeApplication by lazy { InstanceFactory.getInstance(ISVAuthCodeApplication::class.java) }

    companion object {
        private const val CLIENT_TYPE_WORKPLUS_ISV = "WorkPlusISV"
        private const val CLIENT_TYPE_WORK_WEI_XIN = "WorkWeiXin"
    }
    init {
        queryW6sSuiteTicketRoute()
        queryW6sTemporaryAuthCodeRoute()
        queryW6sPermanentAuthCodeRoute()

        queryWorkWeiXinSuiteTicketRoute()
        queryWorkWeiXinPermanentAuthCodeRoute()
    }

    private fun queryW6sSuiteTicketRoute(){
        createGetRoute(path = "/$version/w6s/tickets/:suiteId"){ route ->

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {

                    try {
                        val suiteId = it.pathParam("suiteId")
                        val isvSuiteTicketDTO = isvSuiteTicketApplication.querySuiteTicket(suiteId = suiteId,clientType = CLIENT_TYPE_WORKPLUS_ISV).await()

                        it.jsonFormatEnd(JsonObject.mapFrom(isvSuiteTicketDTO).toBuffer())
                    }catch (t:Throwable){
                        it.fail(t)
                    }

                }
            }

        }
    }

    private fun queryW6sTemporaryAuthCodeRoute(){
        createGetRoute(path = "/$version/w6s/authCode/temporary/:suiteId/:orgId"){ route ->

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {

                    try {
                        val suiteId = it.pathParam("suiteId")
                        val orgId = it.pathParam("orgId")
                        val authCodeDTO = isvAuthCodeApplication.queryTemporaryAuthCode(suiteId = suiteId,domainId = "workplus",orgCode = orgId,clientType = CLIENT_TYPE_WORKPLUS_ISV).await()
                        it.jsonFormatEnd(JsonObject.mapFrom(authCodeDTO).toBuffer())
                    }catch (t:Throwable){
                        it.fail(t)
                    }

                }
            }

        }
    }

    private fun queryW6sPermanentAuthCodeRoute(){
        createGetRoute(path = "/$version/w6s/authCode/pernament/:suiteId/:orgId"){ route ->

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {

                    try {
                        val suiteId = it.pathParam("suiteId")
                        val orgId = it.pathParam("orgId")
                        val authCodeDTO = isvAuthCodeApplication.queryPermanentAuthCode(suiteId = suiteId,domainId = "workplus",orgCode = orgId,clientType = CLIENT_TYPE_WORKPLUS_ISV).await()
                        it.jsonFormatEnd(JsonObject.mapFrom(authCodeDTO).toBuffer())
                    }catch (t:Throwable){
                        it.fail(t)
                    }

                }
            }

        }
    }




    private fun queryWorkWeiXinSuiteTicketRoute(){
        createGetRoute(path = "/$version/weixin/tickets/:suiteId"){ route ->

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {

                    try {
                        val suiteId = it.pathParam("suiteId")
                        val isvSuiteTicketDTO = isvSuiteTicketApplication.querySuiteTicket(suiteId = suiteId,clientType = CLIENT_TYPE_WORK_WEI_XIN).await()

                        it.jsonFormatEnd(JsonObject.mapFrom(isvSuiteTicketDTO).toBuffer())
                    }catch (t:Throwable){
                        it.fail(t)
                    }

                }
            }

        }
    }

    private fun queryWorkWeiXinPermanentAuthCodeRoute(){
        createGetRoute(path = "/$version/weixin/authCode/:suiteId/:orgId"){ route ->

            route.handler {
                GlobalScope.launch(vertx.dispatcher()) {

                    try {
                        val suiteId = it.pathParam("suiteId")
                        val orgId = it.pathParam("orgId")
                        val authCodeDTO = isvAuthCodeApplication.queryPermanentAuthCode(suiteId = suiteId,domainId = "WorkWeiXin",orgCode = orgId,clientType = "WorkWeiXin").await()
                        it.jsonFormatEnd(JsonObject.mapFrom(authCodeDTO).toBuffer())
                    }catch (t:Throwable){
                        it.fail(t)
                    }

                }
            }

        }
    }


}