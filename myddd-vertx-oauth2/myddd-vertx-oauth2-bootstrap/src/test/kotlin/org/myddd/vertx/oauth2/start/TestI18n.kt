package org.myddd.vertx.oauth2.start

import io.vertx.config.ConfigStoreOptions
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import io.vertx.core.json.JsonObject

import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.junit5.VertxTestContext
import java.util.*


@ExtendWith(VertxExtension::class)
class TestI18n {

    @Test
    fun testRead(){
        println(Locale.getDefault())
        val i18nInputStream = this::class.java.classLoader.getResourceAsStream("META-INF/i18n/default.properties")
        Assertions.assertNotNull(i18nInputStream)
    }

    @Test
    fun testVertxRead(vertx: Vertx,testContext: VertxTestContext){
        val fileStore = ConfigStoreOptions()
            .setType("file")
            .setFormat("properties")
            .setConfig(JsonObject().put("path", "META-INF/i18n/default.properties"))

        val options = ConfigRetrieverOptions()
            .addStore(fileStore)

        val configRetriever = ConfigRetriever.create(Vertx.vertx(), options)

        configRetriever.getConfig {
            if(it.succeeded()){
                val config = it.result()
                println("config,$config")
                testContext.completeNow()
            }
            testContext.failNow("not success")
        }
    }
}