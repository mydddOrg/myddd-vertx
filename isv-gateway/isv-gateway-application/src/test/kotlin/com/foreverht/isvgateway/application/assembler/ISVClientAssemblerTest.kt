package com.foreverht.isvgateway.application.assembler

import com.foreverht.isvgateway.AbstractTest
import com.foreverht.isvgateway.api.dto.ISVClientDTO
import com.foreverht.isvgateway.api.dto.ISVClientExtraDTO
import com.foreverht.isvgateway.api.dto.extra.ISVClientExtraForWorkPlusDTO
import com.foreverht.isvgateway.domain.ISVClient
import com.foreverht.isvgateway.domain.ISVClientExtra
import com.foreverht.isvgateway.domain.ISVClientType
import com.foreverht.isvgateway.domain.extra.ISVClientExtraForWorkPlusApp
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.oauth2.domain.OAuth2Client
import java.util.*

class ISVClientAssemblerTest : AbstractTest() {

    @Test
    fun testToISVClientDTO(){
        val isvClient = ISVClient()
        isvClient.clientId = UUID.randomUUID().toString()
        isvClient.clientName = UUID.randomUUID().toString()
        isvClient.description = UUID.randomUUID().toString()
        isvClient.callback = UUID.randomUUID().toString()

        val isvClientExtra = ISVClientExtraForWorkPlusApp()
        isvClientExtra.clientId = UUID.randomUUID().toString()
        isvClientExtra.clientSecret = UUID.randomUUID().toString()
        isvClientExtra.domainId = UUID.randomUUID().toString()
        isvClientExtra.api = UUID.randomUUID().toString()
        isvClientExtra.ownerId = UUID.randomUUID().toString()

        isvClient.extra = isvClientExtra

        val oauth2 = OAuth2Client()
        oauth2.clientId = isvClient.clientId
        oauth2.clientSecret = UUID.randomUUID().toString()
        isvClient.oauth2Client = oauth2

        val isvClientDTO = toISVClientDTO(isvClient)

        Assertions.assertNotNull(isvClientDTO)
        Assertions.assertEquals(isvClient.clientId,isvClientDTO.clientId)
        Assertions.assertEquals(isvClient.oauth2Client.clientSecret,isvClientDTO.clientSecret)
        Assertions.assertNotNull(isvClientDTO.extra)
    }

    @Test
    fun testToISVClient(){
        val isvClientExtraDTO = ISVClientExtraForWorkPlusDTO(
            clientId = randomIDString.randomString(),
            clientSecret = randomIDString.randomString(),
            domainId = randomIDString.randomString(),
            api = randomIDString.randomString(),
            ownerId = randomIDString.randomString()
        )

        val isvClientDTO = ISVClientDTO(clientName = UUID.randomUUID().toString(),extra = isvClientExtraDTO,callback = UUID.randomUUID().toString(),clientId = UUID.randomUUID().toString())

        val isvClient = toISVClient(isvClientDTO)
        Assertions.assertNotNull(isvClient)
        Assertions.assertEquals(ISVClientType.WorkPlusApp,isvClient.clientType)
        Assertions.assertEquals(isvClientDTO.callback,isvClient.callback)
    }

    @Test
    fun testToISVClientExtra(){
        val isvClientExtraDTO = ISVClientExtraForWorkPlusDTO(
            clientId = randomIDString.randomString(),
        clientSecret = randomIDString.randomString(),
            domainId = randomIDString.randomString(),
            api = randomIDString.randomString(),
            ownerId = randomIDString.randomString()
        )

        val isvClientExtra = toISVClientExtra(isvClientExtraDTO)

        Assertions.assertNotNull(isvClientExtra)
        Assertions.assertEquals(ISVClientExtraForWorkPlusApp::class.java,isvClientExtra!!::class.java)


        val anotherIsvClientExtraDTO = object : ISVClientExtraDTO(clientType = "NO_EXISTS"){}
        val notExistsISVClientExtra = toISVClientExtra(anotherIsvClientExtraDTO)
        Assertions.assertNull(notExistsISVClientExtra)
    }

    @Test
    fun testToISVClientExtraDTO(){
        val isvClientExtra = ISVClientExtraForWorkPlusApp()
        isvClientExtra.clientId = UUID.randomUUID().toString()
        isvClientExtra.clientSecret = UUID.randomUUID().toString()
        isvClientExtra.domainId = UUID.randomUUID().toString()
        isvClientExtra.api = UUID.randomUUID().toString()
        isvClientExtra.ownerId = UUID.randomUUID().toString()

        val isvClientExtraDTO = toISVClientExtraDTO(isvClientExtra)

        Assertions.assertNotNull(isvClientExtra)
        Assertions.assertEquals(ISVClientExtraForWorkPlusDTO::class.java,isvClientExtraDTO!!::class.java)

        val notExistsExtra = object : ISVClientExtra() {}
        val notExistsExtraDTO = toISVClientExtraDTO(notExistsExtra)

        Assertions.assertNull(notExistsExtraDTO)
    }
}