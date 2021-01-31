package org.myddd.vertx.querychannel.api

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestPageParam {

    @Test
    fun testPageParam() {
        val pageParam = PageParam(page = 10,pageSize = 1)
        Assertions.assertEquals(pageParam.page ,10)
        Assertions.assertEquals(pageParam.pageSize,1)

        val defaultPagParam = PageParam()
        Assertions.assertEquals(defaultPagParam.page ,0)
        Assertions.assertEquals(defaultPagParam.pageSize,20)
    }
}