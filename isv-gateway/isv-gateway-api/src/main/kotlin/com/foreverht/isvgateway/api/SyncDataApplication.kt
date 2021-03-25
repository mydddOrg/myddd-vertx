package com.foreverht.isvgateway.api

import io.vertx.core.Future

interface SyncDataApplication {

    suspend fun syncOrganization(clientId:String,domainId:String,orgCode:String):Future<Unit>

}