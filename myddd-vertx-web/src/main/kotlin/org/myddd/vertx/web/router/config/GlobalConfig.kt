package org.myddd.vertx.web.router.config

import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.impl.future.PromiseImpl
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*

object GlobalConfig {

    private val vertx by lazy {InstanceFactory.getInstance(Vertx::class.java)}

    private var configObject : JsonObject? = null

    fun getConfig():JsonObject? {
        return configObject
    }

    suspend fun loadGlobalConfig():Future<Unit>{
        val promise = PromiseImpl<Unit>()

        if(Objects.isNull(configObject)){
            try {
                configObject = loadConfigFromFile().await()
                promise.onSuccess(Unit)
            }catch (e:Exception){
                promise.fail(e)
            }
        }
        return promise.future()
    }

    private suspend fun loadConfigFromFile():Future<JsonObject>{
        val promise = PromiseImpl<JsonObject>()

        try {


            val path = "config.properties"

            val configFileStore = ConfigStoreOptions()
                .setType("file")
                .setFormat("properties")
                .setConfig(JsonObject().put("path", path))

            val options = ConfigRetrieverOptions()
                .addStore(configFileStore)

            val configRetriever = ConfigRetriever.create(vertx, options)

            val config = configRetriever.config.await()

            promise.onSuccess(config)
        }catch (e:Exception){
            promise.fail(e)
        }

        return promise

    }






}