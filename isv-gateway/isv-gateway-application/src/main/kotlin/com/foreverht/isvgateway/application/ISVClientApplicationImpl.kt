package com.foreverht.isvgateway.application

import com.foreverht.isvgateway.api.ISVClientApplication
import com.foreverht.isvgateway.api.dto.ISVClientDTO
import io.vertx.core.Future

class ISVClientApplicationImpl : ISVClientApplication {

    override suspend fun queryClientInfo(clientId: String): Future<ISVClientDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun updateISVClient(isvClientDTO: ISVClientDTO): ISVClientDTO {
        TODO("Not yet implemented")
    }

    override suspend fun createISVClient(isvClientDTO: ISVClientDTO): ISVClientDTO {
        TODO("Not yet implemented")
    }

}