package org.myddd.vertx.junit

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class,IOCInitExtension::class)
class TestVertxTestContextJUnitExt {

    @Test
    fun testExecute(testContext: VertxTestContext){
        testContext.execute {
            Assertions.assertThat(1).isEqualTo(1)
        }
    }

    @Test
    fun testThrown(testContext: VertxTestContext){
        testContext.execute {
            testContext.assertThrow(RuntimeException::class.java){
                doSomething().await()
            }
        }
    }

    private fun doSomething():Future<Void>{
        throw RuntimeException("ERROR")
    }
}