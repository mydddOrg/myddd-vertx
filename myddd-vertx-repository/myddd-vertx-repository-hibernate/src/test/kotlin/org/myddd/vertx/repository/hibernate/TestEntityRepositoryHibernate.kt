package org.myddd.vertx.repository.hibernate

import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.junit.assertNotThrow
import org.myddd.vertx.junit.assertThrow
import org.myddd.vertx.junit.execute
import org.myddd.vertx.repository.api.EntityRepository
import org.myddd.vertx.string.RandomIDString
import java.util.*
import java.util.stream.Stream
import javax.persistence.PersistenceException
import kotlin.collections.ArrayList
import kotlin.random.Random

@ExtendWith(VertxExtension::class,IOCInitExtension::class)
class TestEntityRepositoryHibernate {
    private val randomIDString by lazy { InstanceFactory.getInstance(RandomIDString::class.java) }
    companion object {

        @JvmStatic
        fun parametersRepository():Stream<EntityRepository>{
            return Stream.of(
                EntityRepositoryHibernate()
            )
        }

    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testAdd(repository:EntityRepository, testContext: VertxTestContext){
        testContext.execute {
            val user =  randomUser()
            val created = repository.save(user).await()
            testContext.verify {
                Assertions.assertTrue(created.id > 0)
            }

            created.age = 36
            repository.save(created).await()


            val errorUser =  User(username = randomIDString.randomString(64),age = 35)
            try {
                repository.save(errorUser).await()
                testContext.failNow("不可能到这")
            }catch (t:Throwable){
                testContext.verify { Assertions.assertNotNull(t) }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testRemoveEntity(repository:EntityRepository,testContext: VertxTestContext){
        testContext.execute {

            testContext.assertThrow(PersistenceException::class.java){
                repository.remove(User()).await()
            }

            val user =  randomUser()
            val created = repository.save(user).await()

            testContext.assertNotThrow{
                repository.remove(created).await()
            }

        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testUpdate(repository:EntityRepository,testContext: VertxTestContext){
        testContext.execute {
            val user =  randomUser()
            val createdUser =  repository.save(user).await()
            createdUser.age = 36

            repository.save(createdUser).await()

            val queryUser = repository.get(User::class.java,createdUser.id).await()
            testContext.verify {
                Assertions.assertEquals(queryUser?.age,36)
            }


            val notExistsUser = User(username = randomIDString.randomUUID(),age = 1000)
            try {
                repository.save(notExistsUser).await()
            }catch (t:Throwable){
                testContext.verify { Assertions.assertNotNull(t) }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testFind(repository:EntityRepository,testContext: VertxTestContext){

        testContext.execute {
            val user =  randomUser()
            val createdUser =  repository.save(user).await()
            val queryUser = repository.get(User::class.java,createdUser.id).await()

            if(queryUser == null)testContext.failed()

            val notExistsUser = repository.get(User::class.java,Long.MAX_VALUE).await()
            testContext.verify {
                Assertions.assertFalse(notExistsUser != null)
            }

            try {
                repository.get(NotExistsEntity::class.java,0).await()
                testContext.failNow("不可能到这")
            }catch (t:Throwable){
                testContext.verify { Assertions.assertNotNull(t) }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testExists(repository:EntityRepository,testContext: VertxTestContext){
        testContext.execute {
            val user =  randomUser()
            val createdUser =  repository.save(user).await()
            val exists =repository.exists(User::class.java,createdUser.id).await()
            testContext.verify {
                Assertions.assertTrue(exists)
            }

            try {
                repository.exists(NotExistsEntity::class.java,0).await()
                testContext.failNow("不可能到这")
            }catch (t:Throwable){
                testContext.verify { Assertions.assertNotNull(t) }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testBatchAdd(repository:EntityRepository,testContext: VertxTestContext){
        testContext.execute {
            val users = ArrayList<User>()
            for (i in 1..10){
                users.add(User(username = "lingen_${i}",age = 35 + i))
            }

            val userArray:Array<User> = users.toTypedArray()
            val success = repository.batchSave(userArray).await()
            testContext.verify {
                Assertions.assertTrue(success)
            }

            try {
                val errorEntities = arrayOf(NotExistsEntity())
                repository.batchSave(errorEntities).await()
                testContext.failNow("不可能到这")
            }catch (t:Throwable){
                testContext.verify { Assertions.assertNotNull(t) }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testDelete(repository:EntityRepository,testContext: VertxTestContext){
        testContext.execute {
            repository.delete(User::class.java,Long.MAX_VALUE).await()

            val user =  randomUser()
            val createdUser =  repository.save(user).await()
            var exists = repository.exists(User::class.java,createdUser.id).await()
            if(!exists)testContext.failed()

            repository.delete(User::class.java,createdUser.id).await()
            exists = repository.exists(User::class.java,createdUser.id).await()
            testContext.verify {
                Assertions.assertFalse(exists)
            }

            try {
                repository.delete(NotExistsEntity::class.java,0).await()
                testContext.failNow("不可能到这")
            }catch (t:Throwable){
                testContext.verify { Assertions.assertNotNull(t) }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testQueryList(repository:EntityRepository,testContext: VertxTestContext){
        testContext.execute {
            val user =  randomUser()
            repository.save(user).await()

            var list = repository.listQuery(User::class.java,"from User").await()
            testContext.verify {
                Assertions.assertTrue(list.isNotEmpty())
            }

            list = repository.listQuery(User::class.java,"from User where username = :username", mapOf("username" to user.username)).await()
            testContext.verify {
                Assertions.assertTrue(list.isNotEmpty())
            }

            list = repository.listQuery(User::class.java,"from User where username = :username", mapOf("username" to UUID.randomUUID().toString())).await()
            testContext.verify {
                Assertions.assertTrue(list.isEmpty())
            }

            try {
                repository.listQuery(NotExistsEntity::class.java,"from NotExistsEntity where username = :username",).await()
                testContext.failNow("不可能到这")
            }catch (t:Throwable){
                testContext.verify { Assertions.assertNotNull(t) }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testSingleQuery(repository:EntityRepository,testContext: VertxTestContext){
        testContext.execute {
            val user =  randomUser()
            repository.save(user).await()

            var query = repository.singleQuery(User::class.java,"from User where username = :username", mapOf("username" to user.username)).await()
            testContext.verify {
                Assertions.assertNotNull(query)
            }

            query = repository.singleQuery(User::class.java,"from User where username = :username", mapOf("username" to UUID.randomUUID().toString())).await()
            testContext.verify {
                Assertions.assertNull(query)
            }

            try {
                repository.singleQuery(NotExistsEntity::class.java,"from NotExistsEntity where username = :username",).await()
                testContext.failNow("不可能到这")
            }catch (t:Throwable){
                testContext.verify { Assertions.assertNotNull(t) }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testInTransaction(repository:EntityRepository,testContext: VertxTestContext){
        testContext.execute {
            val repositoryJpa = repository as EntityRepositoryHibernate
            val results = repositoryJpa.inTransaction { session ->
                session.persist(randomUser()).chain { _ -> session.persist(randomUser()) }
            }
            testContext.verify { Assertions.assertNotNull(results) }
        }
    }


    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testExecuteUpdate(repository:EntityRepository,testContext: VertxTestContext){
        testContext.execute {
            val user =  randomUser()
            repository.save(user).await()

            val updated = repository.executeUpdate("update User set age = :age", mapOf("age" to 40)).await()
            testContext.verify {
                Assertions.assertTrue(updated!! > 0L)
            }

            val queryUser = repository.singleQuery(User::class.java,"from User where username = :username", mapOf("username" to user.username)).await()
            testContext.verify {
                Assertions.assertNotNull(queryUser)
                Assertions.assertEquals(queryUser!!.age,40)
            }

            try {
                repository.executeUpdate("update NotExistsEntity set age = :age", mapOf("age" to 40)).await()
                testContext.failNow("不可能到这")
            }catch (t:Throwable){
                testContext.verify { Assertions.assertNotNull(t) }
            }
        }
    }

    private fun randomUser():User {
        return User(username = randomIDString.randomString(),age = Random.nextInt(10,50))
    }

}