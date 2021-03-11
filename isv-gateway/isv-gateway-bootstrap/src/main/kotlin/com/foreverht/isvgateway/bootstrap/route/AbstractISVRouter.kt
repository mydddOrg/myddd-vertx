package com.foreverht.isvgateway.bootstrap.route

import com.foreverht.isvgateway.api.ISVClientApplication
import com.foreverht.isvgateway.api.OrganizationApplication
import com.foreverht.isvgateway.domain.ISVErrorCode
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.oauth2.api.OAuth2Application
import org.myddd.vertx.web.router.AbstractRouter
import java.util.*

abstract class AbstractISVRouter(vertx: Vertx, router: Router): AbstractRouter(vertx = vertx,router = router) {

    private val organizationApplicationMap:Map<String,OrganizationApplication> = mapOf(
        "WorkPlusApp" to InstanceFactory.getInstance(OrganizationApplication::class.java,"WorkPlusApp")
    )

    private val oauth2Application:OAuth2Application by lazy { InstanceFactory.getInstance(OAuth2Application::class.java) }

    private val isvClientApplication:ISVClientApplication by lazy { InstanceFactory.getInstance(ISVClientApplication::class.java) }

    suspend fun getOrganizationApplication(accessToken:String):Future<OrganizationApplication>{
        return try {
            val clientId = oauth2Application.queryValidClientIdByAccessToken(accessToken).await()
            val isvClient = isvClientApplication.queryClientByClientId(clientId).await()
            if(Objects.nonNull(isvClient)){
                Future.succeededFuture(organizationApplicationMap[isvClient!!.extra.clientType])
            }else{
                throw BusinessLogicException(ISVErrorCode.CLIENT_ID_NOT_FOUND)
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }


}