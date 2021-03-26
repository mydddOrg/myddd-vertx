package org.myddd.vertx.querychannel.api

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestPageParam {

    @Test
    fun testPageParam() {
        val pageParam = PageParam(limit = 10,skip = 0)
        Assertions.assertEquals(pageParam.skip ,0)
        Assertions.assertEquals(pageParam.limit,10)

        val defaultPagParam = PageParam()
        Assertions.assertEquals(defaultPagParam.skip ,0)
        Assertions.assertEquals(defaultPagParam.limit,20)
    }
}