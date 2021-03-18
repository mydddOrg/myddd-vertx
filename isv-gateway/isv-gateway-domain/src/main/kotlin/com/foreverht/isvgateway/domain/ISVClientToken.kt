package com.foreverht.isvgateway.domain

import com.foreverht.isvgateway.domain.converter.ISVClientTokenExtraConverter
import io.vertx.core.Future
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.domain.BaseEntity
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*
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

        fun createInstanceByExtra(clientId: String,extra:ISVClientTokenExtra):ISVClientToken{
            val clientToken = ISVClientToken()
            clientToken.clientId = clientId
            clientToken.clientType = extra.clientType
            clientToken.extra = extra
            clientToken.token = extra.accessToken()
            return clientToken
        }

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
        return try{
            val exists = queryByClientId(clientId = this.clientId).await()
            return if(Objects.isNull(exists)){
                repository.save(this)
            }else{
                exists!!.extra = this.extra
                exists.token = this.token
                repository.save(exists)
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

}