package com.foreverht.isvgateway.api.dto

import io.vertx.kotlin.core.json.array
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class EmployeeDTOTest {

    @Test
    fun testCreateEmployeeDTO(){

        val jsonObject =  json {
            obj(
                "id" to UUID.randomUUID().toString(),
                "nickname" to UUID.randomUUID().toString(),
                "avatar" to UUID.randomUUID().toString(),
                "mobile" to UUID.randomUUID().toString(),
                "positions" to array(
                    obj(
                        "org_id" to UUID.randomUUID().toString(),
                        "code" to UUID.randomUUID().toString(),
                        "path" to UUID.randomUUID().toString(),
                        "job_title" to UUID.randomUUID().toString(),
                        "primary" to true
                    )
                )
            )
        }

        val employeeDTO = EmployeeDTO.createInstanceFromJsomObject(jsonObject)

        Assertions.assertNotNull(employeeDTO)

    }

}