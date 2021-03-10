package com.foreverht.isvgateway.api.dto

import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class PositionDTOTest {

    @Test
    fun testPositionDTO(){

        val jsonObject =  json {
            obj(
                "org_id" to UUID.randomUUID().toString(),
                "code" to UUID.randomUUID().toString(),
                "path" to UUID.randomUUID().toString(),
                "job_title" to UUID.randomUUID().toString(),
                "primary" to true
            )
        }
        val positionDTO = PositionDTO.createInstanceFromJsonObject(jsonObject = jsonObject)

        Assertions.assertNotNull(positionDTO)

    }
}