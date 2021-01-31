package org.myddd.vertx.querychannel.api

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestPage {

    @Test
    fun testPage(){
        val page = Page<Any>(dataList = emptyList(),pageSize = 10,totalCount = 20,page = 1)
        Assertions.assertEquals(page.page,1)
        Assertions.assertEquals(page.totalCount,20)
        Assertions.assertEquals(page.pageSize,10)
        Assertions.assertTrue(page.dataList.isEmpty())
    }
}