package com.foreverht.isvgateway.application

import com.foreverht.isvgateway.api.ISVAuthCodeApplication
import com.foreverht.isvgateway.api.dto.ISVAuthCodeDTO
import com.foreverht.isvgateway.application.assembler.toISVAuthCode
import com.foreverht.isvgateway.application.assembler.toISVAuthDTO
import com.foreverht.isvgateway.domain.ISVAuthCode
import com.foreverht.isvgateway.domain.ISVClientType
import io.vertx.core.Future
import io.vertx.kotlin.coroutines.await
import java.util.*

class ISVAuthCodeApplicationImpl:ISVAuthCodeApplication {

    override suspend fun createTemporaryAuthCode(authCode: ISVAuthCodeDTO): Future<ISVAuthCodeDTO?> {
        return try {
            val isvAuthCode = toISVAuthCode(authCode)
            val created = isvAuthCode.createTemporaryAuth().await()
            Future.succeededFuture(toISVAuthDTO(created))
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun toPermanent(authCode:ISVAuthCodeDTO): Future<ISVAuthCodeDTO?> {
        return try {
            val isvAuthCode = toISVAuthCode(authCode)
            val permanent = isvAuthCode.toPermanent().await()
            Future.succeededFuture(toISVAuthDTO(permanent))
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun queryTemporaryAuthCode(suiteId: String, orgId:String,clientType: String): Future<ISVAuthCodeDTO?> {
        return try {
            val authCode = ISVAuthCode.queryTemporaryAuthCode(suiteId = suiteId,orgId = orgId,clientType = ISVClientType.valueOf(clientType)).await()
            if(Objects.nonNull(authCode)){
                Future.succeededFuture(toISVAuthDTO(authCode!!))
            }else{
                Future.succeededFuture()
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun queryPermanentAuthCode(suiteId: String, orgId:String,clientType: String): Future<ISVAuthCodeDTO?> {
        return try {
            val authCode = ISVAuthCode.queryPermanentAuthCode(suiteId = suiteId,orgId = orgId,clientType = ISVClientType.valueOf(clientType)).await()
            if(Objects.nonNull(authCode)){
                Future.succeededFuture(toISVAuthDTO(authCode!!))
            }else{
                Future.succeededFuture()
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}