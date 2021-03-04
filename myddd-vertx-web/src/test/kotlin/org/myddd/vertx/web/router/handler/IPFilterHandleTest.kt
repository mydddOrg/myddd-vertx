package org.myddd.vertx.web.router.handler

import org.apache.commons.net.util.SubnetUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class IPFilterHandleTest {

    @Test
    fun testSubNetInfo(){
        Assertions.assertEquals(true, SubnetUtils("192.168.1.0/24").info.isInRange("192.168.1.1"))
        Assertions.assertEquals(false, SubnetUtils("192.168.1.0/32").info.isInRange("192.168.1.1"))
        Assertions.assertEquals(false, SubnetUtils("192.168.1.0/32").info.isInRange("192.168.1.0"))
        Assertions.assertEquals(true, SubnetUtils("192.168.1.0/16").info.isInRange("192.168.2.0"))
    }
}