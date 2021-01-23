package org.myddd.vertx.repository.hibernate

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.hibernate.reactive.mutiny.Mutiny
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import java.lang.Exception
import javax.persistence.Persistence

@ExtendWith(VertxExtension::class)
class TestEntityRepositoryHibernate {

    private val repository:EntityRepositoryHibernate = EntityRepositoryHibernate()

    init {
        InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
            override fun configure() {
                bind(Mutiny.SessionFactory::class.java).toInstance(Persistence.createEntityManagerFactory("default")
                    .unwrap(Mutiny.SessionFactory::class.java))
            }
        })))
    }

    @Test
    fun testAdd(vertx:Vertx, testContext: VertxTestContext){
        GlobalScope.launch {
            try {
                val user =  User(username = "lingen",age = 35)
                val created = repository.save(user).await()
                Assertions.assertTrue(created.id > 0)
                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }
        }

    }

    @Test
    fun testUpdate(vertx:Vertx, testContext: VertxTestContext){
        GlobalScope.launch {
            try {
                val user =  User(username = "lingen",age = 35)
                val createdUser =  repository.save(user).await()
                createdUser.age = 36

                repository.save(createdUser).await()

                var queryUser = repository.get(User::class.java,createdUser.id).await()
                Assertions.assertEquals(queryUser?.age,36)
                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }
        }
    }

    @Test
    fun testFind(vertx:Vertx, testContext: VertxTestContext){

        GlobalScope.launch {
            try {
                val user =  User(username = "lingen",age = 35)
                val createdUser =  repository.save(user).await()
                var queryUser = repository.get(User::class.java,createdUser.id).await()

                if(queryUser == null)testContext.failed()

                var notExistsUser = repository.get(User::class.java,Long.MAX_VALUE).await()
                Assertions.assertFalse(notExistsUser != null)
                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }

        }
    }

    @Test
    fun testExists(vertx:Vertx, testContext: VertxTestContext){
        GlobalScope.launch {
            try {
                val user =  User(username = "lingen",age = 35)
                val createdUser =  repository.save(user).await()
                var exists =repository.exists(User::class.java,createdUser.id).await()
                Assertions.assertTrue(exists)
                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }

        }
    }

    @Test
    fun testBatchAdd(vertx:Vertx, testContext: VertxTestContext){
        GlobalScope.launch {
            try {
                val users = ArrayList<User>()
                for (i in 1..10){
                    users.add(User(username = "lingen_${i}",age = 35 + i))
                }

                val userArray:Array<User> = users.toTypedArray()
                val success = repository.batchSave(userArray).await();
                Assertions.assertTrue(success)
                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }

        }

    }

    @Test
    fun testDelete(vertx:Vertx, testContext: VertxTestContext){
        GlobalScope.launch {
            try {
                repository.delete(User::class.java,Long.MAX_VALUE).await()

                val user =  User(username = "lingen",age = 35)
                val createdUser =  repository.save(user).await()
                var exists = repository.exists(User::class.java,createdUser.id).await()
                if(!exists)testContext.failed()

                repository.delete(User::class.java,createdUser.id).await()
                exists = repository.exists(User::class.java,createdUser.id).await()
                Assertions.assertFalse(exists)
                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }


        }
    }

}