package org.myddd.vertx.ioc.guice

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.name.Names
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory

class GuiceInstanceProviderTest {

  companion object {

    @BeforeAll
    @JvmStatic
    fun beforeAll(){

      InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
        override fun configure() {
          bind(InterfaceA::class.java).to(ObjectA::class.java)
          bind(InterfaceB::class.java).to(ObjectB::class.java)
          bind(InterfaceA::class.java).annotatedWith(Names.named("AnotherA")).to(ObjectA::class.java)
        }
      })))

    }

  }


  @Test
  fun testGetInstance(){
    val objectA = InstanceFactory.getInstance(ObjectA::class.java)
    Assertions.assertNotNull(objectA)
    Assertions.assertNotNull(objectA.getB())
  }

  @Test
  fun testGetInstanceByName(){
    val objectA = InstanceFactory.getInstance(InterfaceA::class.java)
    val anotherA = InstanceFactory.getInstance(InterfaceA::class.java,"AnotherA")

    Assertions.assertNotNull(anotherA)
    Assertions.assertNotEquals(objectA,anotherA)
  }
}