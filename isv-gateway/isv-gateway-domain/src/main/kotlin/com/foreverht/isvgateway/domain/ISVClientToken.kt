package com.foreverht.isvgateway.domain

import com.foreverht.isvgateway.domain.converter.ISVClientTokenExtraConverter
import io.vertx.core.Future
import org.myddd.vertx.domain.BaseEntity
import org.myddd.vertx.ioc.InstanceFactory
import javax.persistence.*

@Entity
@Table(name = "isv_client_token",
    indexes = [
        Index(name = "index_token_client_id",columnList = "client_id")
    ],
    uniqueConstraints = [UniqueConstraint(columnNames = ["client_id","client_type"])])
class ISVClientToken : BaseEntity() {

    @Column(name = "client_id")
    lateinit var clientId:String

    @Column(name = "client_type")
    lateinit var clientType:ISVClientType

    @Column(name = "client_token")
    lateinit var token:String

    @Column(name = "extra",length = 500)
    @Convert(converter = ISVClientTokenExtraConverter::class)
    lateinit var extra: ISVClientTokenExtra

    companion object {
        private val repository by lazy { InstanceFactory.getInstance(ISVClientRepository::class.java) }

        suspend fun queryByClientId(clientId:String):Future<ISVClientToken?>{
            return repository.singleQuery(ISVClientToken::class.java,"from ISVClientToken where clientId = :clientId",
                mapOf("clientId" to clientId))
        }

        suspend fun saveByExtraToken(tokenExtra: ISVClientTokenExtra,clientId: String):Future<ISVClientToken> {
            return try {
                val isvClientToken = ISVClientToken()
                isvClientToken.clientType = tokenExtra.clientType
                isvClientToken.token = tokenExtra.accessToken()
                isvClientToken.extra = tokenExtra
                isvClientToken.clientId = clientId
                return repository.save(isvClientToken)
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }
    }

    suspend fun saveClientToken():Future<ISVClientToken>{
       return repository.save(this)
    }

}