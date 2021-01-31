package org.myddd.vertx.querychannel.api

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestQueryParam {

    @Test
    fun testCountSQL(){
        val queryParam = QueryParam(Any::class.java,"from Any")
        Assertions.assertEquals(queryParam.countSQL(),"select count(*) from Any")

        Assertions.assertEquals(queryParam.clazz,Any::class.java)
        Assertions.assertEquals(queryParam.sql,"from Any")

        val anotherQueryParam = QueryParam(Any::class.java,"select age from Any where userId = :userId", mapOf("userId" to 1))
        Assertions.assertEquals(anotherQueryParam.countSQL(),"select count(*) from (select age from Any where userId = :userId)")
        Assertions.assertEquals(1,anotherQueryParam.params["userId"])
    }
}