package org.myddd.vertx.ioc

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock

class TestInstanceFactory {

    private val provider:InstanceProvider = mock(InstanceProvider::class.java)

    @Test
    fun testInstanceFactoryWithoutProvider(){
        Assertions.assertThrows(RuntimeException::class.java){
            InstanceFactory.getInstance(A::class.java)
        }

        Assertions.assertThrows(RuntimeException::class.java){
            InstanceFactory.getInstance(A::class.java,"AnotherBean")
        }
    }

    @Test
    fun testInstanceFactory(){
        InstanceFactory.setInstanceProvider(provider)

        Assertions.assertNull(InstanceFactory.getInstance(A::class.java))

        Mockito.`when`(provider.getInstance(A::class.java)).thenReturn(A())
        Assertions.assertNotNull(InstanceFactory.getInstance(A::class.java))

        Assertions.assertNull(InstanceFactory.getInstance(A::class.java,"AnotherBean"))
        Mockito.`when`(provider.getInstance(A::class.java,"AnotherBean")).thenReturn(A())
        Assertions.assertNotNull(InstanceFactory.getInstance(A::class.java,"AnotherBean"))

    }
}