package org.myddd.vertx.oauth2.domain

import io.vertx.core.Future
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.domain.BaseEntity
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.string.RandomIDString
import javax.persistence.*

@Entity
@Table(name = "oauth2_client",
    indexes = [
        Index(name = "index_client_id",columnList = "client_id")
    ],
    uniqueConstraints = [UniqueConstraint(columnNames = ["client_id"])]
)
class OAuth2Client:BaseEntity() {

    @Column(name = "client_id",length = 36)
    lateinit var clientId:String

    @Column(name = "client_secret",length = 36)
    lateinit var clientSecret:String

    @Column(nullable = false,length = 255)
    var name:String = ""

    var disabled:Boolean = false

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
        return try{
            if(name.isEmpty()){
                throw IllegalArgumentException("name不能为空")
            }

            this.created = System.currentTimeMillis()
            this.clientSecret = randomIDString.randomString(32)

            val client = repository.save(this).await()
            Future.succeededFuture(client)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }

    }

    suspend fun renewClientSecret():Future<OAuth2Client>{
        return try {
            this.clientSecret = randomIDString.randomString(32)
            val client = repository.save(this).await()
            Future.succeededFuture(client)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    suspend fun disable():Future<OAuth2Client>{
        val exists = repository.get(OAuth2Client::class.java,id).await()
        requireNotNull(exists)
        exists.disabled = true
        val disabled  = repository.save(exists).await()
        return Future.succeededFuture(disabled)
    }

    suspend fun enable():Future<OAuth2Client>{
        val exists = repository.get(OAuth2Client::class.java,id).await()
        requireNotNull(exists)
        exists.disabled = false
        val disabled  = repository.save(exists).await()
        return Future.succeededFuture(disabled)
    }

}