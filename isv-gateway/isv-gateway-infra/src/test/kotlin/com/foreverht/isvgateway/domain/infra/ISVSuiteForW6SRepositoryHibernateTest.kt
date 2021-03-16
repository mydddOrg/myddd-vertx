package com.foreverht.isvgateway.domain.infra

import com.foreverht.isvgateway.AbstractTest
import com.foreverht.isvgateway.domain.ISVSuiteForW6S
import com.foreverht.isvgateway.domain.ISVSuiteForW6SRepository
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*

class ISVSuiteForW6SRepositoryHibernateTest : AbstractTest()  {

    private val repository by lazy { InstanceFactory.getInstance(ISVSuiteForW6SRepository::class.java) }

    @Test
    fun testQueryISVSuiteBySuiteKey(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = repository.save(randomISVSuite()).await()
                val query = repository.queryISVSuiteBySuiteKey(suiteKey = created.suiteKey).await()

                testContext.verify {
                    Assertions.assertNotNull(query)
                }

                val notExists = repository.queryISVSuiteBySuiteKey(UUID.randomUUID().toString()).await()
                testContext.verify {
                    Assertions.assertNull(notExists)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    private fun randomISVSuite(): ISVSuiteForW6S {
        val isvSuite = ISVSuiteForW6S()
        isvSuite.suiteKey = randomString()
        isvSuite.suiteSecret = randomString()
        isvSuite.token = randomString()
        isvSuite.isvApi = randomString()
        isvSuite.encryptSecret = randomString()
        return isvSuite
    }
}