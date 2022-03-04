package org.myddd.vertx.junit

import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.assertj.core.api.Assertions
import org.myddd.vertx.ioc.InstanceFactory


private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }

suspend fun VertxTestContext.assertNotThrow(execution:suspend () -> Unit){
    try {
        execution()
    }catch (t:Throwable){
        this.verify {
            Assertions.assertThat(t).isNull()
        }
    }
}

suspend fun <T:Throwable> VertxTestContext.assertThrow(clazz: Class<T>,execution:suspend () -> Unit){
    try {
        execution()
    }catch (t:Throwable){
        this.verify {
            Assertions.assertThat(t).isInstanceOf(clazz)
        }
    }
}

suspend fun <T:Throwable> VertxTestContext.assertExactlyThrow(clazz: Class<T>,execution:suspend () -> Unit){
    try {
        execution()
    }catch (t:Throwable){
        this.verify {
            Assertions.assertThat(t).isExactlyInstanceOf(clazz)
        }
    }
}

fun VertxTestContext.execute(execution:suspend () -> Unit){
    val vertxTestContext = this
    GlobalScope.launch(vertx.dispatcher()) {
        try {
            execution()
        }catch (t:Throwable){
            vertxTestContext.failNow(t)
        }
        vertxTestContext.completeNow()
    }
}