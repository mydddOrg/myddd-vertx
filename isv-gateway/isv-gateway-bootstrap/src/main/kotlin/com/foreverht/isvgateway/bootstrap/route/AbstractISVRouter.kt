package com.foreverht.isvgateway.bootstrap.route

import com.foreverht.isvgateway.api.*
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

    companion object {
        const val WorkPlusApp = "WorkPlusApp"

        private val accessTokenApplication by lazy { InstanceFactory.getInstance(AccessTokenApplication::class.java) }

        private val organizationApplicationMap:Map<String,OrganizationApplication> = mapOf(
            WorkPlusApp to InstanceFactory.getInstance(OrganizationApplication::class.java,WorkPlusApp)
        )

        private val employeeApplicationMap:Map<String,EmployeeApplication> = mapOf(
            WorkPlusApp to InstanceFactory.getInstance(EmployeeApplication::class.java,WorkPlusApp)
        )

        private val mediaApplicationMap:Map<String,MediaApplication> = mapOf(
            WorkPlusApp to InstanceFactory.getInstance(MediaApplication::class.java,WorkPlusApp)
        )

        private val appApplicationMap:Map<String,AppApplication> = mapOf(
            WorkPlusApp to InstanceFactory.getInstance(AppApplication::class.java,WorkPlusApp)
        )

        private val messageApplicationMap:Map<String,MessageApplication> = mapOf(
            WorkPlusApp to InstanceFactory.getInstance(MessageApplication::class.java,WorkPlusApp)
        )

        private val oauth2Application:OAuth2Application by lazy { InstanceFactory.getInstance(OAuth2Application::class.java) }

        private val isvClientApplication:ISVClientApplication by lazy { InstanceFactory.getInstance(ISVClientApplication::class.java) }


    }


    suspend fun getMessageApplication(accessToken: String):Future<MessageApplication>{
        return getApplicationByClientType(applicationMap = messageApplicationMap,isvAccessToken = accessToken)
    }

    suspend fun getOrganizationApplication(accessToken:String):Future<OrganizationApplication>{
        return getApplicationByClientType(applicationMap = organizationApplicationMap,isvAccessToken = accessToken)
    }

    suspend fun getEmployeeApplication(accessToken: String):Future<EmployeeApplication>{
        return getApplicationByClientType(applicationMap = employeeApplicationMap,isvAccessToken = accessToken)
    }

    suspend fun getMediaApplication(accessToken: String):Future<MediaApplication>{
        return getApplicationByClientType(applicationMap = mediaApplicationMap,isvAccessToken = accessToken)
    }

    suspend fun getAppApplication(accessToken: String):Future<AppApplication>{
        return getApplicationByClientType(applicationMap = appApplicationMap,isvAccessToken = accessToken)
    }

    private suspend fun <T>  getApplicationByClientType(applicationMap:Map<String,T>, isvAccessToken: String):Future<T>{
        return try {
            val isvClient = accessTokenApplication.queryClientByAccessToken(isvAccessToken).await()
            if(Objects.nonNull(isvClient)){
                Future.succeededFuture(applicationMap[isvClient.extra.clientType])
            }else{
                throw BusinessLogicException(ISVErrorCode.CLIENT_ID_NOT_FOUND)
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

}