package org.myddd.vertx.id

import org.junit.jupiter.api.Test

class SnowflakeDistributeIdTest {

    @Test
    fun testSnowflakeDistributeId(){
        val snowflakeDistributeId = SnowflakeDistributeId(0,0)
        for (i in 1..10){
            println(snowflakeDistributeId.nextId())
        }

    }
}