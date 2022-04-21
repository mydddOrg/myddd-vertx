package org.myddd.vertx.web

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.util.concurrent.atomic.AtomicBoolean


class VerticleExtension : BeforeAllCallback {

    companion object {
        private val ONCE = AtomicBoolean(true)
        lateinit var deployId: String
        const val port: Int = 8080
        val vertx = Vertx.vertx()
    }

    private suspend fun startVerticle(vertx: Vertx): Future<Unit> {
        return try {
            deployId = vertx.deployVerticle(WebVerticle(port = port)).await()
            Future.succeededFuture(Unit)
        } catch (t: Throwable) {
            Future.failedFuture(t)
        }
    }

    override fun beforeAll(context: ExtensionContext?) {
        runBlocking {
            if (ONCE.getAndSet(false)) {
                val job = GlobalScope.launch {
                    startVerticle(vertx).await()
                }
                job.join()
            }
        }
    }
}