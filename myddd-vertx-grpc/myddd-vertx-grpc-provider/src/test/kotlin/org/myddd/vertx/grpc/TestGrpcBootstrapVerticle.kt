package org.myddd.vertx.grpc

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.servicediscovery.ServiceDiscovery
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider


@ExtendWith(VertxExtension::class)
class TestGrpcBootstrapVerticle {

    companion object {

        private val logger by lazy { LoggerFactory.getLogger(TestGrpcBootstrapVerticle::class.java) }
        private lateinit var deployId:String

        @BeforeAll
        @JvmStatic
        fun beforeAll(vertx: Vertx,testContext: VertxTestContext){
            GlobalScope.launch(vertx.dispatcher()) {
                try {

                    InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
                        override fun configure() {
                            bind(Vertx::class.java).toInstance(vertx)
                            bind(GrpcInstanceProvider::class.java).to(ServiceDiscoveryGrpcInstanceProvider::class.java)

                            bind(HealthCheckApplication::class.java)
                        }
                    })))

                    deployId = vertx.deployVerticle(HealthGrpcBootstrapVerticle()).await()
                    Assertions.assertNotNull(deployId)
                }catch (t:Throwable){
                    testContext.failNow(t)
                }
                testContext.completeNow()
            }
        }

        @AfterAll
        @JvmStatic
        fun afterAll(vertx: Vertx,testContext: VertxTestContext){
            GlobalScope.launch(vertx.dispatcher()) {
                try {
                    vertx.undeploy(deployId).await()
                }catch (t:Throwable){
                    testContext.failNow(t)
                }
                testContext.completeNow()
            }
        }
    }



    @Test
    fun testGrpcBootstrapVerticle(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val discovery = ServiceDiscovery.create(vertx)
                val records = discovery.getRecords{
                    true
                }.await()


                for (i in 1..4){
                    val record = discovery.getRecord{
                        it.type.equals("grpc").and(
                            it.location.getString("host").equals("127.0.0.1")
                        )
                    }.await()



                    discovery.getReference(record)


                    logger.debug(record)

                }
                testContext.verify {
                    Assertions.assertTrue(records.isNotEmpty())
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

}