package org.myddd.vertx.json

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(VertxExtension::class)
class AsyncJsonMapperTest {

    @Test
    fun testMapperFrom(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val user = UserDTO(username = UUID.randomUUID().toString(),age = 10)
                val json = JsonObject.mapFrom(user)

                val mappedJson = AsyncJsonMapper.mapFrom(vertx,json.toString(),user::class.java).await()
                testContext.verify {
                    Assertions.assertNotNull(mappedJson)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }
}