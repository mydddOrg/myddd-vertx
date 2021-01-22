package org.myddd.vertx.repository

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider

@ExtendWith(VertxExtension::class)
class TestAbstractRepository {

    private val userRepository by lazy { InstanceFactory.getInstance(UserRepository::class.java) }

    @BeforeEach
    fun beforeAll(vertx: Vertx){
        val injector = Guice.createInjector(object : AbstractModule(){
            override fun configure() {
                bind(Vertx::class.java).toInstance(vertx)
                bind(UserRepository::class.java)
                bind(JDBCRepositoryConfig::class.java)
                    .toInstance(JDBCRepositoryConfig(host = "127.0.0.1",port = 3306,database = "backend",username = "root",password = "admin"))
            }
        })
        InstanceFactory.setInstanceProvider(GuiceInstanceProvider(injector))
    }

    @Test
    fun testVertx(vertx: Vertx,testContext: VertxTestContext){
        Assertions.assertNotNull(InstanceFactory.getInstance(Vertx::class.java))
        testContext.completeNow()
    }

    @Test
    fun testIsConnected(vertx: Vertx, testContext: VertxTestContext){
        userRepository.isConnected().onSuccess { success -> if(success)testContext.completeNow() else testContext.failNow("connection failed") }
    }

    @Test
    fun testIsConnectedBySyncStyle(vertx: Vertx, testContext: VertxTestContext){
        GlobalScope.launch {
            val success = userRepository.isConnected().await()
            if(success)testContext.completeNow() else testContext.failNow("connection failed")
        }
    }

}