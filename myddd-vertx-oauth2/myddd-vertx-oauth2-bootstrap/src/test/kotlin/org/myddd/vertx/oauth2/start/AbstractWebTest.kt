package org.myddd.vertx.oauth2.start

import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.ioc.InstanceFactory

@ExtendWith(VertxExtension::class,VerticleExtension::class)
open class AbstractWebTest {


    companion object {

        var port = 8080

        const val host = "127.0.0.1"

        val webClient by lazy { InstanceFactory.getInstance(WebClient::class.java) }

    }

}