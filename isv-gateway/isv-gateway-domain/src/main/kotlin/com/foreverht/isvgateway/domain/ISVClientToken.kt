package com.foreverht.isvgateway.domain

import com.foreverht.isvgateway.domain.converter.ISVClientTokenExtraConverter
import io.vertx.core.Future
import org.myddd.vertx.domain.BaseEntity
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.string.RandomIDString
import javax.persistence.*

@Entity
@Table(name = "isv_client_token",
    indexes = [
        Index(name = "index_client_id",columnList = "client_id"),
        Index(name = "index_domain_id",columnList = "domain_id"),
        Index(name = "index_org_code",columnList = "org_code"),
        Index(name = "index_org_code",columnList = "client_token")
    ],
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["client_id","domain_id","org_code"]),
        UniqueConstraint(columnNames = ["client_token"])
    ])
class ISVClientToken : BaseEntity() {

    @ManyToOne(cascade = [],fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    lateinit var client:ISVClient

    @Column(name = "domain_id",nullable = false,length = 64)
    lateinit var domainId:String

    @Column(name = "org_code",nullable = false,length = 64)
    lateinit var orgCode:String

    @Column(name = "client_token")
    lateinit var token:String

    @Column(name = "extra",length = 500)
    @Convert(converter = ISVClientTokenExtraConverter::class)
    lateinit var extra: ISVClientTokenExtra

    companion object {
        private val repository by lazy { InstanceFactory.getInstance(ISVClientRepository::class.java) }

        private val randomString by lazy { InstanceFactory.getInstance(RandomIDString::class.java) }

        fun createInstanceByExtra(client: ISVClient,extra:ISVClientTokenExtra,domainId:String,orgCode:String):ISVClientToken{
            val clientToken = ISVClientToken()
            clientToken.client = client
            clientToken.domainId = domainId
            clientToken.orgCode = orgCode
            clientToken.extra = extra
            clientToken.token = randomString.randomString()
            return clientToken
        }

        suspend fun queryClientToken(clientId:String, domainId: String, orgCode: String):Future<ISVClientToken?>{
            return repository.singleQuery(ISVClientToken::class.java,"from ISVClientToken where client.clientId = :clientId and domainId=:domainId and orgCode = :orgCode",
                mapOf(
                    "clientId" to clientId,
                    "domainId" to domainId,
                    "orgCode" to orgCode
                ))
        }

        suspend fun queryByToken(token:String):Future<ISVClientToken?>{
            return repository.singleQuery(ISVClientToken::class.java,"from ISVClientToken where token = :token",
                mapOf(
                    "token" to token
                ))
        }

    }

    suspend fun updateByExtraToken(extra:ISVClientTokenExtra):Future<ISVClientToken>{
        return try {
            this.token = randomString.randomString()
            this.extra = extra
            this.updated = System.currentTimeMillis()
            return repository.save(this)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    suspend fun createClientToken():Future<ISVClientToken>{
        return try{
            this.created = System.currentTimeMillis()
            return repository.save(this)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

}