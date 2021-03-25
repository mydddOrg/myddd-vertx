package com.foreverht.isvgateway.application

import com.foreverht.isvgateway.api.ISVClientApplication
import com.foreverht.isvgateway.api.dto.ISVClientDTO
import com.foreverht.isvgateway.application.assembler.toISVClient
import com.foreverht.isvgateway.application.assembler.toISVClientDTO
import com.foreverht.isvgateway.domain.ISVClient
import io.vertx.core.Future
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.querychannel.api.QueryChannel
import org.myddd.vertx.querychannel.api.QueryParam
import kotlin.streams.toList

class ISVClientApplicationImpl : ISVClientApplication {

    private val queryChannel by lazy { InstanceFactory.getInstance(QueryChannel::class.java) }

    override suspend fun queryClientByClientId(clientId: String): Future<ISVClientDTO?> {
        return try {
            val isvClient = ISVClient.queryClient(clientId).await()
            Future.succeededFuture(isvClient?.let { toISVClientDTO(it) })
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun updateISVClient(isvClientDTO: ISVClientDTO): Future<ISVClientDTO> {
        return try {
            val isvClient = toISVClient(isvClientDTO)
            val updated = isvClient.updateISVClient().await()
            Future.succeededFuture(toISVClientDTO(updated))
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun createISVClient(isvClientDTO: ISVClientDTO): Future<ISVClientDTO> {
        return try {
            val isvClient = toISVClient(isvClientDTO)
            val created = isvClient.createISVClient().await()
            Future.succeededFuture(toISVClientDTO(created))
        }catch (t:Throwable){
            Future.failedFuture(t)
        }

    }

    override suspend fun listAllClients(): Future<List<ISVClientDTO>> {
        return try {
            val lists = queryChannel.queryList(QueryParam(
                clazz = ISVClient::class.java,
                sql = "from ISVClient"
            )).await()
            Future.succeededFuture(lists.stream().map { toISVClientDTO(it) }.toList())
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

}