package org.myddd.vertx.grpc

import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.servicediscovery.Record
import io.vertx.servicediscovery.ServiceDiscovery
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.myddd.vertx.junit.execute

class TestServiceType:AbstractTest() {

    companion object {

        private lateinit var discovery:ServiceDiscovery

        @JvmStatic
        @BeforeAll
        fun beforeAll(testContext: VertxTestContext){
            testContext.execute {
                discovery = ServiceDiscovery.create(vertx)
                testContext.verify {
                    Assertions.assertNotNull(discovery)
                }
            }
        }

    }

    @Test
    fun testPublishService(testContext: VertxTestContext){
        testContext.execute {
            val record = Record().setType("grpc")
                .setLocation(JsonObject().put("ip","127.0.0.0").put("port",8080))
                .setName("test")

            val published = discovery.publish(record).await()

            testContext.verify {
                Assertions.assertNotNull(published)
            }
        }
    }

    @Test
    fun testLookUpService(testContext: VertxTestContext){
        testContext.execute {
            val record = Record().setType("grpc")
                .setLocation(JsonObject().put("ip","127.0.0.0").put("port",8080))
                .setName("test")

            discovery.publish(record).await()

            val discoveryRecord = discovery.getRecord{
                it.name.equals("test")
            }.await()

            testContext.verify {
                Assertions.assertNotNull(discoveryRecord)
            }

            val notFoundRecord = discovery.getRecord{
                it.name.equals("aaa")
            }.await()

            val records = discovery.getRecords{_ -> true}.await()
            testContext.verify {
                println(records)
            }

            testContext.verify {
                Assertions.assertNull(notFoundRecord)
            }
        }
    }

}