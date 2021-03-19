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
        Index(name = "index_org_code",columnList = "org_code"),
        Index(name = "index_domain_id",columnList = "domain_id")
    ],
    uniqueConstraints = [UniqueConstraint(columnNames = ["suite_id","client_type","org_code","domain_id"])]
)
class ISVAuthCode : BaseEntity() {

    @Column(name = "suite_id",nullable = false,length = 64)
    lateinit var suiteId:String

    @Column(name = "client_type",nullable = false,length = 64)
    lateinit var clientType: ISVClientType

    @Column(name = "domain_id",length = 64)
    lateinit var domainId:String

    @Column(name = "org_code",nullable = false,length = 64)
    lateinit var orgCode:String

    @Column(name = "auth_status")
    lateinit var authStatus: ISVAuthStatus

    @Column(name = "temporary_auth_code",nullable = false,length = 128)
    lateinit var temporaryAuthCode:String

    @Column(name = "permanent_auth_code",length = 128)
    var permanentAuthCode:String? = null

    companion object {
        private val repository by lazy { InstanceFactory.getInstance(ISVClientRepository::class.java) }

        suspend fun queryAuthCode(suiteId: String,domainId:String, orgCode:String, clientType:ISVClientType):Future<ISVAuthCode?>{
            return try{
                return repository.queryAuthCode(suiteId = suiteId,domainId = domainId,clientType = clientType,orgCode = orgCode)
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }

        suspend fun queryTemporaryAuthCode(suiteId: String,domainId:String, orgCode:String, clientType: ISVClientType):Future<ISVAuthCode?>{
            return try{
                return repository.queryTemporaryAuthCode(suiteId = suiteId,domainId = domainId,clientType = clientType,orgCode = orgCode)
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }

        suspend fun queryPermanentAuthCode(suiteId: String,domainId:String,orgCode:String, clientType: ISVClientType):Future<ISVAuthCode?>{
            return try{
                return repository.queryPermanentAuthCode(suiteId = suiteId,domainId = domainId, clientType = clientType,orgCode = orgCode)
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }
    }

    suspend fun toPermanent():Future<ISVAuthCode>{
        return try {
            val authCode = repository.queryAuthCode(suiteId = suiteId,domainId = domainId,orgCode = orgCode,clientType = clientType).await()
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
            val exists = queryAuthCode(suiteId = this.suiteId,domainId = domainId,orgCode = this.orgCode,clientType = this.clientType).await()
            return if(Objects.isNull(exists)){
                this.authStatus = ISVAuthStatus.Temporary
                this.created = System.currentTimeMillis()
                repository.save(this)
            }else{
                exists!!.authStatus = ISVAuthStatus.Temporary
                exists.temporaryAuthCode = this.temporaryAuthCode
                exists.updated = System.currentTimeMillis()
                repository.save(exists)
            }

        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }


}