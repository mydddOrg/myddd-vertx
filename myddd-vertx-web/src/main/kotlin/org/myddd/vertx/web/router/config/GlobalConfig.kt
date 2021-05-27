package org.myddd.vertx.web.router.config

import io.vertx.core.Future
import io.vertx.core.Vertx
import org.myddd.vertx.config.Config

@Deprecated("use org.myddd.vertx.config.Config instead")
object GlobalConfig {

    fun getString(key:String,def:String = ""):String {
        return Config.getString(key,def)
    }

    fun getBoolean(key:String,def:Boolean = false):Boolean {
        return Config.getBoolean(key,def)
    }

    fun getInteger(key:String,def:Int = 0):Int {
        return Config.getInteger(key,def)
    }

    suspend fun loadGlobalConfig(vertx: Vertx):Future<Unit>{
        return Config.loadGlobalConfig(vertx)
    }
}