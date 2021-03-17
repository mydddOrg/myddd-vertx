package com.foreverht.isvgateway.domain

import io.vertx.core.Future
import org.myddd.vertx.repository.api.EntityRepository

interface ISVClientRepository : EntityRepository {

    suspend fun querySuiteTicket(suiteId:String,clientType:ISVClientType):Future<ISVSuiteTicket?>

    suspend fun queryAuthCode(suiteId: String,orgId:String,clientType: ISVClientType):Future<ISVAuthCode?>

    suspend fun queryTemporaryAuthCode(suiteId: String,orgId:String,clientType: ISVClientType):Future<ISVAuthCode?>

    suspend fun queryPermanentAuthCode(suiteId: String,orgId:String,clientType: ISVClientType):Future<ISVAuthCode?>
}