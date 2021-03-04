package org.myddd.vertx.web.router.handler

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.commons.net.util.SubnetUtils
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.web.router.config.GlobalConfig

class IPFilterHandle : Handler<RoutingContext> {

    private val logger by lazy { LoggerFactory.getLogger(IPFilterHandle::class.java) }

    private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }

    private val whiteIps:MutableList<SubnetUtils.SubnetInfo> = mutableListOf()

    override fun handle(rc: RoutingContext?) {
        GlobalScope.launch(vertx.dispatcher()) {
            val address = rc?.request()?.remoteAddress()
            val enableIpWhite = GlobalConfig.getConfig()?.getBoolean("ipFilter.white.enable")
            if(enableIpWhite == true){

                var inRange = false
                getWhiteIps().forEach {
                    if(it.isInRange(address?.hostAddress())){
                        inRange = true
                        return@forEach
                    }
                }

                if(inRange) rc?.next() else rc?.response()?.setStatusCode(403)?.end()
            }
            logger.info("request ip: $address")
        }
    }

    private fun getWhiteIps():List<SubnetUtils.SubnetInfo> {
        if(whiteIps.isEmpty()){
            GlobalConfig.getConfig()?.getString("ipFilter.white.list")?.split(",")?.forEach {
                whiteIps.add(SubnetUtils(it).info)
            }
        }
        return whiteIps.toList()
    }

}