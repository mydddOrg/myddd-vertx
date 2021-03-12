package com.foreverht.isvgateway.bootstrap.route

import com.foreverht.isvgateway.api.EmployeeApplication
import com.foreverht.isvgateway.api.ISVClientApplication
import com.foreverht.isvgateway.api.MediaApplication
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
import javax.print.attribute.standard.Media

abstract class AbstractISVRouter(vertx: Vertx, router: Router): AbstractRouter(vertx = vertx,router = router) {

    companion object {
        const val WorkPlusApp = "WorkPlusApp"

        private val organizationApplicationMap:Map<String,OrganizationApplication> = mapOf(
            WorkPlusApp to InstanceFactory.getInstance(OrganizationApplication::class.java,WorkPlusApp)
        )

        private val employeeApplicationMap:Map<String,EmployeeApplication> = mapOf(
            WorkPlusApp to InstanceFactory.getInstance(EmployeeApplication::class.java,WorkPlusApp)
        )

        private val mediaApplicationMap:Map<String,MediaApplication> = mapOf(
            WorkPlusApp to InstanceFactory.getInstance(MediaApplication::class.java,WorkPlusApp)
        )

        private val oauth2Application:OAuth2Application by lazy { InstanceFactory.getInstance(OAuth2Application::class.java) }

        private val isvClientApplication:ISVClientApplication by lazy { InstanceFactory.getInstance(ISVClientApplication::class.java) }
    }



    suspend fun getOrganizationApplication(accessToken:String):Future<OrganizationApplication>{
        return getApplicationByClientType(applicationMap = organizationApplicationMap,accessToken = accessToken)
    }

    suspend fun getEmployeeApplication(accessToken: String):Future<EmployeeApplication>{
        return getApplicationByClientType(applicationMap = employeeApplicationMap,accessToken = accessToken)
    }

    suspend fun getMediaApplication(accessToken: String):Future<MediaApplication>{
        return getApplicationByClientType(applicationMap = mediaApplicationMap,accessToken = accessToken)
    }

    private suspend fun <T>  getApplicationByClientType(applicationMap:Map<String,T>,accessToken: String):Future<T>{
        return try {
            val clientId = oauth2Application.queryValidClientIdByAccessToken(accessToken).await()
            val isvClient = isvClientApplication.queryClientByClientId(clientId).await()
            if(Objects.nonNull(isvClient)){
                Future.succeededFuture(applicationMap[isvClient!!.extra.clientType])
            }else{
                throw BusinessLogicException(ISVErrorCode.CLIENT_ID_NOT_FOUND)
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

}