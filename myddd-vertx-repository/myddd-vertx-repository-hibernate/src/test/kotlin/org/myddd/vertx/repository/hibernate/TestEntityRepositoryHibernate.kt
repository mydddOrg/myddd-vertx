package org.myddd.vertx.repository.hibernate

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.name.Names
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.hibernate.reactive.mutiny.Mutiny
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.myddd.vertx.id.IDGenerator
import org.myddd.vertx.id.SnowflakeDistributeId
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.repository.api.EntityRepository
import java.util.*
import java.util.stream.Stream
import javax.persistence.Persistence
import kotlin.Exception
import kotlin.collections.ArrayList

@ExtendWith(VertxExtension::class)
class TestEntityRepositoryHibernate {

    init {
        InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
            override fun configure() {
                bind(IDGenerator::class.java).toInstance(SnowflakeDistributeId())

                bind(Mutiny.SessionFactory::class.java).toInstance(Persistence.createEntityManagerFactory("default")
                    .unwrap(Mutiny.SessionFactory::class.java))

                bind(Mutiny.SessionFactory::class.java).annotatedWith(Names.named("pg")).toInstance(Persistence.createEntityManagerFactory("pg")
                    .unwrap(Mutiny.SessionFactory::class.java))

            }
        })))
    }

    companion object {

        @JvmStatic
        private fun parametersRepository():Stream<EntityRepository>{
            return Stream.of(
                EntityRepositoryHibernate(),
                EntityRepositoryHibernate(dataSource = "pg")
            )
        }

    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testAdd(repository:EntityRepository,vertx: Vertx, testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val user =  User(username = "lingen",age = 35)
                val created = repository.save(user).await()
                testContext.verify {
                    Assertions.assertTrue(created.id > 0)
                }

                val anotherUser = User(username = "lingen",age = 35)
                try {
                    repository.save(anotherUser).await()
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }
                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }
        }

    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testUpdate(repository:EntityRepository,vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val user =  User(username = "lingen",age = 35)
                val createdUser =  repository.save(user).await()
                createdUser.age = 36

                repository.save(createdUser).await()

                val queryUser = repository.get(User::class.java,createdUser.id).await()
                testContext.verify {
                    Assertions.assertEquals(queryUser?.age,36)
                }


                val notExistsUser = User(username = "anotherUser",age = 1000)
                try {
                    repository.save(notExistsUser).await()
                }catch (t:Throwable){
                    testContext.verify { Assertions.assertNotNull(t) }
                }
                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testFind(repository:EntityRepository,vertx: Vertx,testContext: VertxTestContext){

        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val user =  User(username = "lingen",age = 35)
                val createdUser =  repository.save(user).await()
                val queryUser = repository.get(User::class.java,createdUser.id).await()

                if(queryUser == null)testContext.failed()

                val notExistsUser = repository.get(User::class.java,Long.MAX_VALUE).await()
                testContext.verify {
                    Assertions.assertFalse(notExistsUser != null)
                }
                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }

        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testExists(repository:EntityRepository,vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val user =  User(username = "lingen",age = 35)
                val createdUser =  repository.save(user).await()
                val exists =repository.exists(User::class.java,createdUser.id).await()
                testContext.verify {
                    Assertions.assertTrue(exists)
                }
                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }

        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testBatchAdd(repository:EntityRepository,vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val users = ArrayList<User>()
                for (i in 1..10){
                    users.add(User(username = "lingen_${i}",age = 35 + i))
                }

                val userArray:Array<User> = users.toTypedArray()
                val success = repository.batchSave(userArray).await()
                testContext.verify {
                    Assertions.assertTrue(success)
                }
                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }

        }

    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testDelete(repository:EntityRepository,vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                repository.delete(User::class.java,Long.MAX_VALUE).await()

                val user =  User(username = "lingen",age = 35)
                val createdUser =  repository.save(user).await()
                var exists = repository.exists(User::class.java,createdUser.id).await()
                if(!exists)testContext.failed()

                repository.delete(User::class.java,createdUser.id).await()
                exists = repository.exists(User::class.java,createdUser.id).await()
                testContext.verify {
                    Assertions.assertFalse(exists)
                }
                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testQueryList(repository:EntityRepository,vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val user =  User(username = "lingen",age = 35)
                repository.save(user).await()

                var list = repository.listQuery(User::class.java,"from User").await()
                testContext.verify {
                    Assertions.assertTrue(list.isNotEmpty())
                }

                list = repository.listQuery(User::class.java,"from User where username = :username", mapOf("username" to "lingen")).await()
                testContext.verify {
                    Assertions.assertTrue(list.isNotEmpty())
                }

                list = repository.listQuery(User::class.java,"from User where username = :username", mapOf("username" to UUID.randomUUID().toString())).await()
                testContext.verify {
                    Assertions.assertTrue(list.isEmpty())
                }

                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }

        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testSingleQuery(repository:EntityRepository,vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {

                val user =  User(username = "lingen",age = 35)
                repository.save(user).await()

                var query = repository.singleQuery(User::class.java,"from User").await()
                testContext.verify {
                    Assertions.assertNotNull(query)
                }

                query = repository.singleQuery(User::class.java,"from User where username = :username", mapOf("username" to "lingen")).await()
                testContext.verify {
                    Assertions.assertNotNull(query)
                }

                query = repository.singleQuery(User::class.java,"from User where username = :username", mapOf("username" to UUID.randomUUID().toString())).await()
                testContext.verify {
                    Assertions.assertNull(query)
                }

                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }
        }
    }


    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testExecuteUpdate(repository:EntityRepository,vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val user =  User(username = "lingen",age = 35)
                repository.save(user).await()

                val updated = repository.executeUpdate("update User set age = :age", mapOf("age" to 40)).await()
                testContext.verify {
                    Assertions.assertTrue(updated!! > 0L)
                }

                val queryUser = repository.singleQuery(User::class.java,"from User where username = :username", mapOf("username" to "lingen")).await()
                testContext.verify {
                    Assertions.assertNotNull(queryUser)
                    Assertions.assertEquals(queryUser!!.age,40)
                }

                testContext.completeNow()

            }catch (e:Exception){
                testContext.failNow(e)
            }
        }
    }

}