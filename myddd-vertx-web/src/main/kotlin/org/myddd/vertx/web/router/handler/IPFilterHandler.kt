package org.myddd.vertx.web.router.handler

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.web.router.config.GlobalConfig
import java.util.*



class IPFilterHandle : Handler<RoutingContext> {

    private val logger by lazy { LoggerFactory.getLogger(IPFilterHandle::class.java) }

    private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }

    companion object {
        const val WHITE_LIST_ENABLE = "ipFilter.whitelist.enable"
        const val WHITE_LIST_VALUES = "ipFilter.whitelist.values"
        const val BLACK_LIST_ENABLE = "ipFilter.blacklist.enable"
        const val BLACK_LIST_VALUES = "ipFilter.blacklist.values"

        init {
            reloadCache()
        }


        private var enableWhiteList = false

        private var enableBlackList = false

        private var whiteList:List<String>? = emptyList()


        private var blackList:List<String>? = emptyList()

        fun reloadCache(){
            enableWhiteList = GlobalConfig.getConfig()?.getBoolean(WHITE_LIST_ENABLE) == true
            enableBlackList = GlobalConfig.getConfig()?.getBoolean(BLACK_LIST_ENABLE) == true
            whiteList = GlobalConfig.getConfig()?.getString(WHITE_LIST_VALUES)?.split(",")
            blackList = GlobalConfig.getConfig()?.getString(BLACK_LIST_VALUES)?.split(",")
        }

    }




    override fun handle(rc: RoutingContext?) {
        GlobalScope.launch(vertx.dispatcher()) {
            val address = rc?.request()?.remoteAddress()

            if(enableWhiteList){
                var inRange = false
                whiteList?.forEach {
                    if(checkIPMatching(it,address?.hostAddress())){
                        inRange = true
                        return@forEach
                    }
                }
                if(inRange) rc?.next() else rc?.response()?.setStatusCode(403)?.end()
            }
            else if(enableBlackList){
                var inRange = false
                blackList?.forEach {
                    if(checkIPMatching(it,address?.hostAddress())){
                        inRange = true
                        return@forEach
                    }
                }
                if(!inRange) rc?.next() else rc?.response()?.setStatusCode(403)?.end()
            }else{
                rc?.next()
            }
            logger.info("request ip: $address")
        }
    }

    private fun checkIPMatching(pattern: String, address: String?): Boolean {
        if(Objects.isNull(address))return false

        if (pattern == "*.*.*.*" || pattern == "*") return true
        val mask = pattern.split("\\.").toTypedArray()
        val ipAddress = address!!.split("\\.").toTypedArray()
        for (i in mask.indices) {
            if (mask[i] == "*" || mask[i] == ipAddress[i]) continue else if (mask[i].contains("-")) {
                val min = mask[i].split("-").toTypedArray()[0].toByte()
                val max = mask[i].split("-").toTypedArray()[1].toByte()
                val ip = ipAddress[i].toByte()
                if (ip < min || ip > max) return false
            } else return false
        }
        return true
    }

}