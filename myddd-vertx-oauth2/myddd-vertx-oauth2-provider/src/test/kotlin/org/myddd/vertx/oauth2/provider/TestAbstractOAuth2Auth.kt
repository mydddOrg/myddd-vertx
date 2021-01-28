package org.myddd.vertx.oauth2.provider

import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.oauth2.OAuth2Auth
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestAbstractOAuth2Auth {

    private val oauth2Auth:OAuth2Auth = MydddVertXOAuth2Provider()

    @Test
    fun testJWKSet(){
        Assertions.assertThrows(UnsupportedOperationException::class.java){
            oauth2Auth.jWKSet()
        }
    }

    @Test
    fun testMissingKeyHandler(){
        Assertions.assertThrows(UnsupportedOperationException::class.java){
            oauth2Auth.missingKeyHandler{

            }
        }
    }

    @Test
    fun testAuthorizeURL(){
        Assertions.assertThrows(UnsupportedOperationException::class.java){
            oauth2Auth.authorizeURL(JsonObject())
        }
    }

    @Test
    fun testUserInfo(){
        Assertions.assertThrows(UnsupportedOperationException::class.java){
            oauth2Auth.userInfo(null)
        }
    }

    @Test
    fun testEndSessionURL(){
        Assertions.assertThrows(UnsupportedOperationException::class.java){
            oauth2Auth.endSessionURL(null)
        }
    }

    @Test
    fun testIntrospectToken(){
        Assertions.assertThrows(UnsupportedOperationException::class.java){
            oauth2Auth.introspectToken(null)
        }
    }

    @Test
    fun testGetFlowType(){
        Assertions.assertThrows(UnsupportedOperationException::class.java){
            oauth2Auth.flowType
        }
    }

    @Test
    fun testRbacHandler(){
        Assertions.assertThrows(UnsupportedOperationException::class.java){
            oauth2Auth.rbacHandler(null)
        }
    }
}