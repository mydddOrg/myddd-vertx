package org.myddd.vertx.querychannel.api

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestPage {

    @Test
    fun testPage(){
        val page = Page<Any>(dataList = emptyList(),limit = 10,totalCount = 20,skip = 0)
        Assertions.assertEquals(page.skip,0)
        Assertions.assertEquals(page.totalCount,20)
        Assertions.assertEquals(page.limit,10)
        Assertions.assertTrue(page.dataList.isEmpty())
    }
}