package com.foreverht.isvgateway.api.dto

import io.vertx.core.json.JsonObject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class OrganizationDTOTest {

    @Test
    fun testCreateOrganizationDTO(){
        val jsonObject = JsonObject()
            .put("id", UUID.randomUUID().toString())
            .put("org_code",UUID.randomUUID().toString())
            .put("domain_id",UUID.randomUUID().toString())
            .put("name",UUID.randomUUID().toString())
            .put("logo",UUID.randomUUID().toString())
            .put("path",UUID.randomUUID().toString())

        Assertions.assertNotNull(OrganizationDTO.createInstanceFromJsonObject(jsonObject))
    }


}