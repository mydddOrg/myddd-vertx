package org.myddd.vertx.media.infra

import io.vertx.core.Future
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.junit5.VertxExtension
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.id.StringIDGenerator
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.media.domain.Media
import java.util.*

@ExtendWith(VertxExtension::class,IOCInitExtension::class)
abstract class AbstractTest {

    val logger by lazy { LoggerFactory.getLogger(AbstractTest::class.java) }

    companion object {

        private val idGenerate by lazy { InstanceFactory.getInstance(StringIDGenerator::class.java) }
    }

    protected fun randomString(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }

    protected suspend fun createMedia(): Future<Media> {
        return try {
            val path = AbstractTest::class.java.classLoader.getResource("META-INF/my_avatar.png")!!.path
            val media = Media.createByLocalFile(path = path).await()
            Future.succeededFuture(media)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}