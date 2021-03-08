package org.myddd.vertx.oauth2.domain

import io.vertx.core.Future
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.domain.BaseEntity
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.string.RandomIDString
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "oauth2_client",
    indexes = [
        Index(name = "index_client_id",columnList = "client_id")
    ],
    uniqueConstraints = [UniqueConstraint(columnNames = ["client_id"])]
)
class OAuth2Client:BaseEntity() {

    @Column(name = "client_id")
    lateinit var clientId:String

    @Column(name = "client_secret")
    lateinit var clientSecret:String

    @Column(nullable = false)
    var name:String = ""

    var disabled:Boolean = false

    @Column(name = "display_name")
    var displayName:String? = null

    @Column(name = "description")
    var description:String? = null

    companion object {
        private val randomIDString by lazy { InstanceFactory.getInstance(RandomIDString::class.java) }
        private val repository: OAuth2ClientRepository by lazy { InstanceFactory.getInstance(OAuth2ClientRepository::class.java) }

        fun createInstance(name:String):OAuth2Client {
            val client = OAuth2Client()
            client.clientId = randomIDString.randomString(32)
            client.clientSecret = randomIDString.randomString(32)
            client.name = name
            return client
        }
    }

    suspend fun createClient():Future<OAuth2Client>{
        if(name.isNullOrEmpty()){
            throw BusinessLogicException(OAuth2ErrorCode.CLIENT_NAME_CAN_NOT_NULL)
        }

        this.created = System.currentTimeMillis()
        this.clientSecret = randomIDString.randomString(32)

        return repository.save(this)
    }

    suspend fun renewClientSecret():Future<OAuth2Client>{
        this.clientSecret = randomIDString.randomString(32)
        return repository.save(this)
    }

    suspend fun disable():Future<OAuth2Client>{
        return try {
            this.disabled = true
            val disabled  =repository.save(this).await()
            Future.succeededFuture(disabled)
        }catch (e:Exception){
            Future.failedFuture(e)
        }
    }

    suspend fun enable():Future<OAuth2Client>{
        return try {
            this.disabled = false
            val enabled = repository.save(this).await()
            Future.succeededFuture(enabled)
        }catch (e:Exception){
            Future.failedFuture(e)
        }
    }

}