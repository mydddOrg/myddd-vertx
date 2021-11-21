package org.myddd.vertx.junit

import io.vertx.junit5.VertxTestContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.assertj.core.api.Assertions

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
            Assertions.assertThat(t).isExactlyInstanceOf(clazz)
        }
    }
}

fun VertxTestContext.execute(execution:suspend () -> Unit){
    val vertxTestContext = this
    GlobalScope.launch {
        try {
            execution()
        }catch (t:Throwable){
            vertxTestContext.failNow(t)
        }
        vertxTestContext.completeNow()
    }
}