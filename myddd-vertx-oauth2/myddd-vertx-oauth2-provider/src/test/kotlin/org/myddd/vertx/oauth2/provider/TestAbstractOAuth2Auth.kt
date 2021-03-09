package org.myddd.vertx.oauth2.provider

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.oauth2.OAuth2Auth
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class TestAbstractOAuth2Auth {

    companion object {

        lateinit var oauth2Auth:MydddVertXOAuth2Provider

        @BeforeAll
        @JvmStatic
        fun beforeAll(vertx: Vertx, testContext: VertxTestContext){
            oauth2Auth = MydddVertXOAuth2Provider()
            testContext.completeNow()
        }
    }

    @Test
    fun testJWKSet(testContext: VertxTestContext){
        Assertions.assertThrows(UnsupportedOperationException::class.java){
            oauth2Auth.jWKSet()
        }
        testContext.completeNow()
    }

    @Test
    fun testMissingKeyHandler(testContext: VertxTestContext){
        Assertions.assertThrows(UnsupportedOperationException::class.java){
            oauth2Auth.missingKeyHandler{
            }
        }
        testContext.completeNow()
    }

    @Test
    fun testAuthorizeURL(testContext: VertxTestContext){
        Assertions.assertThrows(UnsupportedOperationException::class.java){
            oauth2Auth.authorizeURL(JsonObject())
        }
        testContext.completeNow()
    }

    @Test
    fun testUserInfo(testContext: VertxTestContext){
        Assertions.assertThrows(UnsupportedOperationException::class.java){
            oauth2Auth.userInfo(null)
        }
        testContext.completeNow()
    }

    @Test
    fun testEndSessionURL(testContext: VertxTestContext){
        Assertions.assertThrows(UnsupportedOperationException::class.java){
            oauth2Auth.endSessionURL(null)
        }
        testContext.completeNow()
    }

    @Test
    fun testIntrospectToken(testContext: VertxTestContext){
        Assertions.assertThrows(UnsupportedOperationException::class.java){
            oauth2Auth.introspectToken(null)
        }
        testContext.completeNow()
    }

    @Test
    fun testGetFlowType(testContext: VertxTestContext){
        Assertions.assertThrows(UnsupportedOperationException::class.java){
            oauth2Auth.flowType
        }
        testContext.completeNow()
    }

    @Test
    fun testRbacHandler(testContext: VertxTestContext){
        Assertions.assertThrows(UnsupportedOperationException::class.java){
            oauth2Auth.rbacHandler(null)
        }
        testContext.completeNow()
    }
}