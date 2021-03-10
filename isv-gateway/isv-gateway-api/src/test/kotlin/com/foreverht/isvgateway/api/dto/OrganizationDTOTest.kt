package com.foreverht.isvgateway.api.dto

import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class OrganizationDTOTest {

    @Test
    fun testCreateOrganizationDTO(){
        val jsonObject = json {
            obj(
                "id" to UUID.randomUUID().toString(),
                "org_code" to UUID.randomUUID().toString(),
                "domain_id" to UUID.randomUUID().toString(),
                "name" to UUID.randomUUID().toString(),
                "logo" to UUID.randomUUID().toString(),
                "path" to UUID.randomUUID().toString()
            )
        }

        Assertions.assertNotNull(OrganizationDTO.createInstanceFromJsonObject(jsonObject))
    }


}