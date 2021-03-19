package com.foreverht.isvgateway.domain

import io.vertx.core.Future
import org.myddd.vertx.repository.api.EntityRepository

interface ISVClientRepository : EntityRepository {

    suspend fun querySuiteTicket(suiteId:String,clientType:ISVClientType):Future<ISVSuiteTicket?>

    suspend fun queryAuthCode(suiteId: String, domainId:String, orgCode:String, clientType: ISVClientType):Future<ISVAuthCode?>

    suspend fun queryTemporaryAuthCode(suiteId: String, domainId:String,orgCode:String, clientType: ISVClientType):Future<ISVAuthCode?>

    suspend fun queryPermanentAuthCode(suiteId: String, domainId:String, orgCode:String, clientType: ISVClientType):Future<ISVAuthCode?>
}