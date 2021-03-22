package com.foreverht.isvgateway.application

import com.foreverht.isvgateway.api.ISVSuiteTicketApplication
import com.foreverht.isvgateway.api.dto.ISVAuthCodeDTO
import com.foreverht.isvgateway.api.dto.ISVSuiteTicketDTO
import com.foreverht.isvgateway.application.assembler.toISVSuiteTicket
import com.foreverht.isvgateway.application.assembler.toISVSuiteTicketDTO
import com.foreverht.isvgateway.domain.ISVClientType
import com.foreverht.isvgateway.domain.ISVSuiteTicket
import io.vertx.core.Future
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*

class ISVSuiteTicketApplicationImpl : ISVSuiteTicketApplication {

    private val w6SBossApplication by lazy { InstanceFactory.getInstance(W6SBossApplication::class.java) }

    override suspend fun saveSuiteTicket(suiteTicket: ISVSuiteTicketDTO): Future<Boolean> {
        return try {
            val isvSuiteTicket = toISVSuiteTicket(isvSuiteTicketDTO = suiteTicket)
            isvSuiteTicket.saveSuiteTicket().await()
            Future.succeededFuture(true)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun querySuiteTicket(suiteId: String, clientType: String): Future<ISVSuiteTicketDTO> {
        return try {
            val isvSuiteTicket = ISVSuiteTicket.querySuiteTicket(suiteId = suiteId,clientType = ISVClientType.valueOf(clientType)).await()
            if(Objects.nonNull(isvSuiteTicket)){
                Future.succeededFuture(toISVSuiteTicketDTO(isvSuiteTicket!!))
            }else{
                Future.failedFuture("suite not found")
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun activeSuite(clientId: String, domainId: String, orgCode: String):Future<Unit> {
        return try {
            w6SBossApplication.requestPermanentCode(clientId = clientId,domainId = domainId,orgCode = orgCode).await()
            w6SBossApplication.activeSuite(clientId = clientId,domainId = domainId,orgCode = orgCode).await()
            Future.succeededFuture()
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }


}