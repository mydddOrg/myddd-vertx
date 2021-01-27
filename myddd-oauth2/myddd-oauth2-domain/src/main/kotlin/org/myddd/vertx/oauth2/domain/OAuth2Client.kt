package org.myddd.vertx.oauth2.domain

import io.vertx.core.Future
import io.vertx.core.impl.future.PromiseImpl
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.domain.BaseEntity
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Index
import javax.persistence.Table

@Entity
@Table(name = "oauth2_client",
    indexes = [
        Index(name = "index_client_id",columnList = "client_id")
    ])
class OAuth2Client:BaseEntity() {

    @Column(name = "client_id")
    lateinit var clientId:String

    @Column(name = "client_secret")
    lateinit var clientSecret:String

    @Column(nullable = false)
    var name:String = ""

    @Column(name = "display_name")
    var displayName:String? = null

    @Column(name = "description")
    var description:String? = null

    companion object {
        val repository: OAuth2ClientRepository by lazy { InstanceFactory.getInstance(OAuth2ClientRepository::class.java) }
    }

    suspend fun createClient():Future<OAuth2Client>{
        check(name.isNotEmpty()){
            "NAME_CAN_NOT_EMPTY"
        }
        this.created = System.currentTimeMillis()
        this.clientId = UUID.randomUUID().toString()
        this.clientSecret = UUID.randomUUID().toString()

        return repository.save(this)
    }

    suspend fun renewClientSecret():Future<OAuth2Client>{
        this.clientSecret = UUID.randomUUID().toString()
        return repository.save(this)
    }

}