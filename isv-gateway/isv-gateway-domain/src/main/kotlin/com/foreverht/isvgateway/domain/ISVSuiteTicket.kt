package com.foreverht.isvgateway.domain

import io.vertx.core.Future
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.domain.BaseEntity
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "isv_suite_ticket",
    indexes = [
        Index(name = "index_suite_id",columnList = "suite_id"),
        Index(name = "index_client_type",columnList = "client_type")
    ],
    uniqueConstraints = [UniqueConstraint(columnNames = ["suite_id","client_type"])]
)
class ISVSuiteTicket : BaseEntity() {

    @Column(name = "suite_id",nullable = false,length = 64)
    lateinit var suiteId:String

    @Column(name = "client_type",nullable = false,length = 64)
    lateinit var clientType: ISVClientType

    @Column(name = "suite_ticket",nullable = false,length = 64)
    lateinit var suiteTicket:String

    companion object {

        private val repository by lazy { InstanceFactory.getInstance(ISVClientRepository::class.java) }

        suspend fun querySuiteTicket(suiteId:String, clientType:ISVClientType):Future<ISVSuiteTicket?>{
            return try {
                return repository.querySuiteTicket(suiteId, clientType)
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }
    }

    suspend fun saveSuiteTicket():Future<ISVSuiteTicket>{
        return try {
            val query = querySuiteTicket(suiteId = this.suiteId,clientType = this.clientType).await()
            return if(Objects.nonNull(query)){
                query!!.suiteTicket = this.suiteTicket
                query.updated = System.currentTimeMillis()
                repository.save(query)
            }else{
                this.updated = System.currentTimeMillis()
                repository.save(this)
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

}