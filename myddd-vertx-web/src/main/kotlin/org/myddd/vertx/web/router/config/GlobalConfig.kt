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

    private lateinit var vertx:Vertx

    internal var configObject : JsonObject? = null

    fun getString(key:String,def:String = ""):String {
        return configObject!!.getString(key,def)
    }

    fun getBoolean(key:String,def:Boolean = false):Boolean {
        return configObject!!.getBoolean(key,def)
    }

    fun getInteger(key:String,def:Int = 0):Int {
        return configObject!!.getInteger(key,def)
    }


    suspend fun loadGlobalConfig(vertx: Vertx):Future<Unit>{
        this.vertx = vertx
        val promise = PromiseImpl<Unit>()

        if(Objects.isNull(configObject)){
            try {
                configObject = loadConfigFromFile().await()
                promise.onSuccess(Unit)
            }catch (e:Exception){
                promise.fail(e)
            }
        }else{
            promise.onSuccess(Unit)
        }
        return promise.future()
    }

    private suspend fun loadConfigFromFile():Future<JsonObject>{
        val promise = PromiseImpl<JsonObject>()

        try {
            //从外部读取变量
            var path = System.getProperty("config")
            if(path.isNullOrEmpty()){
                path = "META-INF/config.properties"
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
        }catch (e:Exception){
            e.printStackTrace()
            promise.fail(e)
        }

        return promise

    }






}