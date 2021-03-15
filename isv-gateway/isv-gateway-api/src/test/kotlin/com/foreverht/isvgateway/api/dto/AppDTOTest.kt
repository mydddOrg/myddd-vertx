package com.foreverht.isvgateway.api.dto

import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class AppDTOTest {

    @Test
    fun testCreateAppDTO(){
        val jsonObject = json {
            obj(
                "app_id" to UUID.randomUUID().toString(),
                "name" to UUID.randomUUID().toString(),
                "icon" to UUID.randomUUID().toString()
            )
        }

        val appDTO = AppDTO.createInstanceFromJson(jsonObject)
        Assertions.assertNotNull(appDTO)
    }
}