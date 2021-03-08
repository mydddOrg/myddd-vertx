package org.myddd.vertx.domain

import org.junit.jupiter.api.Test

class SnowflakeDistributeIdTest {

    @Test
    fun testSnowflakeDistributeId(){
        val snowflakeDistributeId = SnowflakeDistributeId(0,0)
        for (i in 1..1000){
            println(snowflakeDistributeId.nextId())
        }

    }
}