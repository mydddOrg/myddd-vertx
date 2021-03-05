package com.foreverht.isvgateway.domain

import io.vertx.core.Future
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.domain.BaseEntity
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.oauth2.domain.OAuth2Client
import org.myddd.vertx.string.RandomIDString
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "isv_client",
    indexes = [
        Index(name = "index_client_id",columnList = "client_id")
    ],
    uniqueConstraints = [UniqueConstraint(columnNames = ["client_id"])])
class ISVClient : BaseEntity() {

    @Column(name = "client_id")
    lateinit var clientId:String

    @OneToOne(fetch = FetchType.EAGER,cascade = [CascadeType.ALL])
    @JoinColumn(name = "relate_id", referencedColumnName = "id",nullable = false)
    lateinit var oauth2Client:OAuth2Client

    @Column(name = "callback",nullable = false)
    lateinit var callback:String

    @Column(name = "extra")
    @Convert(converter = ISVClientExtraConverter::class)
    lateinit var extra: ISVClientExtra

    @Column(name = "display_name")
    var clientName:String? = null

    @Column(name = "description")
    var description:String? = null



    companion object {

        private val randomIDString by lazy { InstanceFactory.getInstance(RandomIDString::class.java) }
        private val repository by lazy { InstanceFactory.getInstance(ISVClientRepository::class.java) }

        fun createClient(clientName:String, callback:String, extra:ISVClientExtra, description:String? = null):ISVClient{
            val oAuth2Client = OAuth2Client()
            oAuth2Client.clientId = randomIDString.randomString(32)
            oAuth2Client.clientSecret = randomIDString.randomString(32)

            val client = ISVClient()
            client.clientName= clientName
            client.oauth2Client = oAuth2Client
            client.clientId = oAuth2Client.clientId
            client.extra = extra
            client.callback = callback
            client.description = description

            return client
        }

        suspend fun queryClient(clientId:String):Future<ISVClient?>{
            return repository.singleQuery(ISVClient::class.java,"from ISVClient where clientId = :clientId", mapOf("clientId" to clientId))
        }

    }

    suspend fun createISVClient():Future<ISVClient>{
        return repository.save(this)
    }

    suspend fun updateISVClient():Future<ISVClient>{
        return try {
            requireNotNull(this.clientId)

            val client = queryClient(this.clientId).await()
            requireNotNull(client)

            if(!this.clientName.isNullOrEmpty())client.clientName = this.clientName
            if(!this.callback.isNullOrEmpty())client.callback = this.callback
            if(!this.description.isNullOrEmpty())client.description = this.description
            if(Objects.nonNull(this.extra))client.extra = extra

            return repository.save(client)

        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    suspend fun resetSecret():Future<ISVClient>{
        return try {
            requireNotNull(this.clientId)

            val client = queryClient(this.clientId).await()
            requireNotNull(client)

            client.oauth2Client.clientSecret = randomIDString.randomString(32)
            return repository.save(client)

        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}