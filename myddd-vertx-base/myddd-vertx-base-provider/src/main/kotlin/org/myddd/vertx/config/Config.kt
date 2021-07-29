package org.myddd.vertx.config

import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.impl.future.PromiseImpl
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await
import java.util.*

object Config {

    private lateinit var vertx: Vertx

    private val logger by lazy { LoggerFactory.getLogger(Config::class.java) }

    private val systemPropertyKeys by lazy {
        System.getProperties().keys
    }

    var configObject: JsonObject? = null

    fun getString(key: String, def: String = ""): String {
        return when {
            systemPropertyKeys.contains(key) -> System.getProperty(key)
            Objects.nonNull(configObject) -> configObject!!.getString(key, def)
            else -> def
        }
    }

    fun getBoolean(key: String, def: Boolean = false): Boolean {
        return when {
            systemPropertyKeys.contains(key) -> System.getProperty(key).toBoolean()
            Objects.nonNull(configObject) -> configObject!!.getBoolean(key, def)
            else -> def
        }
    }

    fun getInteger(key: String, def: Int = 0): Int {
        return when {
            systemPropertyKeys.contains(key) -> System.getProperty(key).toInt()
            Objects.nonNull(configObject) -> configObject!!.getInteger(key, def)
            else -> def
        }
    }

    fun getLong(key: String, def: Long = 0L): Long {
        return when {
            systemPropertyKeys.contains(key) -> System.getProperty(key).toLong()
            Objects.nonNull(configObject) -> configObject!!.getLong(key, def)
            else -> def
        }
    }


    suspend fun loadGlobalConfig(vertx: Vertx): Future<Unit> {
        Config.vertx = vertx
        val promise = PromiseImpl<Unit>()

        if (Objects.isNull(configObject)) {
            try {
                configObject = loadConfigFromFile().await()
                promise.onSuccess(Unit)
            } catch (e: Exception) {
                promise.fail(e)
            }
        } else {
            promise.onSuccess(Unit)
        }
        return promise.future()
    }

    private suspend fun loadConfigFromFile(): Future<JsonObject> {
        val promise = PromiseImpl<JsonObject>()

        try {
            //从外部读取变量
            var path = System.getProperty("config")
            if (path.isNullOrEmpty()) {
                path = "META-INF/config.properties"
            }

            val pathExists = vertx.fileSystem().exists(path).await()
            if (!pathExists) {
                logger.warn("config path not exists:${path}")
                return Future.succeededFuture()
            }

            val configFileStore = ConfigStoreOptions()
                .setType("file")
                .setFormat("properties")
                .setConfig(JsonObject().put("path", path))

            val options = ConfigRetrieverOptions()
                .addStore(configFileStore)

            val configRetriever = ConfigRetriever.create(vertx, options)

            val config = configRetriever.config.await()

            promise.onSuccess(config)
        } catch (e: Exception) {
            e.printStackTrace()
            promise.fail(e)
        }

        return promise

    }


}