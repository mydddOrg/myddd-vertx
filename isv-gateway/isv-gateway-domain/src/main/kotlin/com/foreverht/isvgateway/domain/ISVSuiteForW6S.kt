package com.foreverht.isvgateway.domain

import com.foreverht.isvgateway.domain.extra.ISVClientExtraForWorkPlusISV
import io.vertx.core.Future
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.domain.BaseEntity
import org.myddd.vertx.ioc.InstanceFactory
import javax.persistence.*

@Entity
@Table(name = "w6s_isv_suite",
    indexes = [
        Index(name = "index_w6s_suite_key",columnList = "suite_key")
    ],
    uniqueConstraints = [UniqueConstraint(columnNames = ["suite_key"])]
)
class ISVSuiteForW6S : BaseEntity(){

    @Column(name = "suite_key",nullable = false,length = 64)
    lateinit var suiteKey:String

    @Column(name = "suite_secret",nullable = false,length = 64)
    lateinit var suiteSecret:String

    @Column(name = "token",nullable = false,length = 64)
    lateinit var token:String

    @Column(name = "encrypt_secret",nullable = false,length = 64)
    lateinit var encryptSecret:String

    @Column(name = "isv_api",nullable = false,length = 64)
    lateinit var isvApi:String

    companion object {

        private val repository:ISVSuiteForW6SRepository by lazy { InstanceFactory.getInstance(ISVSuiteForW6SRepository::class.java) }

        suspend fun queryBySuiteKey(suiteKey:String):Future<ISVSuiteForW6S?>{
            return try {
                return repository.queryISVSuiteBySuiteKey(suiteKey = suiteKey)
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }

        fun createInstanceFromClientExtra(extra:ISVClientExtraForWorkPlusISV):ISVSuiteForW6S {
            val instance = ISVSuiteForW6S()
            instance.suiteKey = extra.suiteKey
            instance.suiteSecret = extra.suiteSecret
            instance.token = extra.token
            instance.encryptSecret = extra.token
            instance.isvApi = extra.isvApi
            return instance
        }
    }

    suspend fun createISVSuite(): Future<ISVSuiteForW6S> {
        return try {

            return repository.save(this)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    suspend fun updateISVSuite():Future<ISVSuiteForW6S> {
        return try {
            val query:ISVSuiteForW6S? = queryBySuiteKey(this.suiteKey).await()
            checkNotNull(query)

            query.suiteSecret = this.suiteSecret
            query.token = this.token
            query.encryptSecret = this.encryptSecret
            query.isvApi = this.isvApi
            query.updated = System.currentTimeMillis()
            return repository.save(query)
        }catch (t:Throwable){ISVSuiteForW6S
            Future.failedFuture(t)
        }
    }



}