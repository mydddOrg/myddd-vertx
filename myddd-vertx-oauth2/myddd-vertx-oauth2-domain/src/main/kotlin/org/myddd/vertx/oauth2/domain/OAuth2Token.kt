package org.myddd.vertx.oauth2.domain

import io.vertx.core.Future
import io.vertx.core.impl.future.PromiseImpl
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.domain.BaseEntity
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "oauth2_token",
    indexes = [
        Index(name = "index_client_id",columnList = "client_id"),
        Index(name = "index_access_token",columnList = "access_token")
    ],
    uniqueConstraints = [UniqueConstraint(columnNames = ["client_id"])])
class OAuth2Token : BaseEntity() {

    @Column(name = "client_id",length = 36)
    lateinit var clientId:String

    @Column(name = "access_token",length = 128)
    lateinit var accessToken:String

    @Column(name = "refresh_token",length = 128)
    lateinit var refreshToken:String

    @Column(name = "access_expired_in")
    var accessExpiredIn:Long = 0

    @Column(name = "refresh_expired_in")
    var refreshExpiredIn:Long = 0

    companion object {
        private val repository:OAuth2TokenRepository by lazy { InstanceFactory.getInstance(OAuth2TokenRepository::class.java) }

        suspend fun createTokenFromClient(client:OAuth2Client):Future<OAuth2Token>{
            val future = PromiseImpl<OAuth2Token>()
            val token = OAuth2Token()
            token.clientId = client.clientId
            token.created = System.currentTimeMillis()
            token.accessToken = UUID.randomUUID().toString()
            token.accessExpiredIn = System.currentTimeMillis() + 1000 * 60 * 60
            token.refreshToken = UUID.randomUUID().toString()
            token.refreshExpiredIn = System.currentTimeMillis() + 1000 * 60 * 60 * 24

            val created = repository.save(token).await()
            future.onSuccess(created)
            return future
        }
    }

    /**
     * 刷新Token
     */
    suspend fun refreshToken():Future<OAuth2Token>{
        val future = PromiseImpl<OAuth2Token>()
        this.updated = System.currentTimeMillis()
        this.accessToken = UUID.randomUUID().toString()
        this.accessExpiredIn = System.currentTimeMillis() + 1000 * 60 * 60
        this.refreshToken = UUID.randomUUID().toString()
        this.refreshExpiredIn = System.currentTimeMillis() + 1000 * 60 * 60 * 24

        val updated =  repository.save(this).await()
        future.onSuccess(updated)
        return future
    }


}