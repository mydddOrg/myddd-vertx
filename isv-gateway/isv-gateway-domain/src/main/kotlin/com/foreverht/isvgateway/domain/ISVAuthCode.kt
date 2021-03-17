package com.foreverht.isvgateway.domain

import io.vertx.core.Future
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.domain.BaseEntity
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "isv_auth_code",
    indexes = [
        Index(name = "index_suite_id",columnList = "suite_id"),
        Index(name = "index_client_type",columnList = "client_type"),
        Index(name = "index_client_type",columnList = "org_id")
    ],
    uniqueConstraints = [UniqueConstraint(columnNames = ["suite_id","client_type","org_id"])]
)
class ISVAuthCode : BaseEntity() {

    @Column(name = "suite_id",nullable = false,length = 64)
    lateinit var suiteId:String

    @Column(name = "client_type",nullable = false,length = 64)
    lateinit var clientType: ISVClientType

    @Column(name = "domain_id",length = 64)
    var domainId:String? = ""

    @Column(name = "org_id",nullable = false,length = 64)
    lateinit var orgId:String

    @Column(name = "auth_status")
    lateinit var authStatus: ISVAuthStatus

    @Column(name = "temporary_auth_code",nullable = false,length = 128)
    lateinit var temporaryAuthCode:String

    @Column(name = "permanent_auth_code",length = 128)
    var permanentAuthCode:String? = null

    companion object {
        private val repository by lazy { InstanceFactory.getInstance(ISVClientRepository::class.java) }

        suspend fun queryAuthCode(suiteId: String,orgId:String,clientType: ISVClientType):Future<ISVAuthCode?>{
            return try{
                return repository.queryAuthCode(suiteId = suiteId,clientType = clientType,orgId = orgId)
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }

        suspend fun queryTemporaryAuthCode(suiteId: String,orgId:String,clientType: ISVClientType):Future<ISVAuthCode?>{
            return try{
                return repository.queryTemporaryAuthCode(suiteId = suiteId,clientType = clientType,orgId = orgId)
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }

        suspend fun queryPermanentAuthCode(suiteId: String,orgId:String,clientType: ISVClientType):Future<ISVAuthCode?>{
            return try{
                return repository.queryPermanentAuthCode(suiteId = suiteId,clientType = clientType,orgId = orgId)
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }
    }

    suspend fun toPermanent():Future<ISVAuthCode>{
        return try {
            val authCode = repository.queryAuthCode(suiteId = suiteId,orgId = orgId,clientType = clientType).await()
            if(Objects.nonNull(authCode)){
                authCode!!.authStatus = ISVAuthStatus.Permanent
                authCode.permanentAuthCode = permanentAuthCode
                return repository.save(authCode)
            }else{
                throw BusinessLogicException(ISVErrorCode.SUITE_ID_NOT_FOUND)
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    suspend fun createTemporaryAuth():Future<ISVAuthCode>{
        return try {
            this.authStatus = ISVAuthStatus.Temporary
            this.created = System.currentTimeMillis()
            return repository.save(this)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }


}