package org.myddd.vertx.repository.hibernate

import io.smallrye.mutiny.Uni
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.junit.assertNotThrow
import org.myddd.vertx.junit.assertThrow
import org.myddd.vertx.junit.execute
import org.myddd.vertx.repository.api.EntityRepository
import org.myddd.vertx.repository.api.EntityRepositoryUni
import org.myddd.vertx.repository.api.SessionObject
import org.myddd.vertx.repository.hibernate.EntityRepositoryTransaction.withTransaction
import org.myddd.vertx.string.RandomIDString
import java.util.*
import java.util.stream.Stream
import javax.persistence.PersistenceException
import kotlin.random.Random

@ExtendWith(VertxExtension::class,IOCInitExtension::class)
class TestEntityRepositoryUni {

    private val randomIDString by lazy { InstanceFactory.getInstance(RandomIDString::class.java) }

    companion object {

        @JvmStatic
        fun parametersRepository(): Stream<EntityRepositoryUni> {
            return Stream.of(
                EntityRepositoryHibernate()
            )
        }

    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testAdd(repository: EntityRepositoryUni, testContext: VertxTestContext){
        testContext.execute {
            val user =  randomUser()
            val created = withTransaction {
                repository.save(it,user)
            }.await()

            testContext.verify {
                Assertions.assertTrue(created.id > 0)
            }

            created.age = 36

            withTransaction {
                repository.save(it,created)
            }.await()

            val errorUser =  User(username = randomIDString.randomString(64),age = 35)

            testContext.assertThrow(PersistenceException::class.java){
                withTransaction {
                    repository.save(it,errorUser)
                }.await()
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testRemoveEntity(repository:EntityRepositoryUni,testContext: VertxTestContext){
        testContext.execute {

            testContext.assertThrow(PersistenceException::class.java){
                withTransaction { repository.remove(it,User()) }.await()
            }

            val user =  randomUser()
            val created = withTransaction {
                repository.save(it,user)
            }.await()

            testContext.assertNotThrow{
                withTransaction { repository.delete(it,User::class.java,created.id) }.await()
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testUpdate(repository:EntityRepositoryUni,testContext: VertxTestContext){
        testContext.execute {
            val user =  randomUser()
            val createdUser = withTransaction { repository.save(it,user) }.await()

            createdUser.age = 36

            withTransaction { repository.save(it,createdUser) }.await()

            val queryUser = withTransaction { repository.get(it,User::class.java,createdUser.id) }.await()

            testContext.verify {
                Assertions.assertEquals(queryUser?.age,36)
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testFind(repository:EntityRepositoryUni,testContext: VertxTestContext){

        testContext.execute {
            val user =  randomUser()
            val createdUser = withTransaction { repository.save(it,user) }.await()
            val queryUser = withTransaction { repository.get(it,User::class.java,createdUser.id)}.await()
            if(queryUser == null)testContext.failed()
            val notExistsUser = withTransaction {repository.get(it,User::class.java,Long.MAX_VALUE)}.await()
            testContext.verify {
                Assertions.assertFalse(notExistsUser != null)
            }
            testContext.assertThrow(PersistenceException::class.java){
                withTransaction { repository.get(it,NotExistsEntity::class.java,0) }.await()
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testExists(repository: EntityRepositoryUni, testContext: VertxTestContext){
        testContext.execute {
            val user =  randomUser()
            val createdUser = withTransaction { repository.save(it,user) }.await()
            val exists = withTransaction { repository.exists(it,User::class.java,createdUser.id) }.await()
            testContext.verify {
                Assertions.assertTrue(exists)
            }

            testContext.assertThrow(PersistenceException::class.java){
                withTransaction {repository.exists(it,NotExistsEntity::class.java,0)}.await()
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testBatchAdd(repository:EntityRepositoryUni,testContext: VertxTestContext){
        testContext.execute {
            val users = ArrayList<User>()
            for (i in 1..10){
                users.add(User(username = "lingen_${i}",age = 35 + i))
            }

            val userArray:Array<User> = users.toTypedArray()
            val success = withTransaction { repository.batchSave(it,userArray) }.await()
            testContext.verify {
                Assertions.assertTrue(success)
            }

            val errorEntities = arrayOf(NotExistsEntity())

            testContext.assertThrow(PersistenceException::class.java){
                withTransaction {repository.batchSave(it,errorEntities)}.await()
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testDelete(repository:EntityRepositoryUni,testContext: VertxTestContext){
        testContext.execute {
            withTransaction {repository.delete(it,User::class.java,Long.MAX_VALUE)}.await()

            val user =  randomUser()
            val createdUser = withTransaction {repository.save(it,user) }.await()

            var exists = withTransaction { repository.exists(it,User::class.java,createdUser.id) }.await()
            testContext.verify { Assertions.assertTrue(exists) }

            withTransaction {repository.delete(it,User::class.java,createdUser.id) }.await()

            exists = withTransaction { repository.exists(it,User::class.java,createdUser.id) }.await()
            testContext.verify {
                Assertions.assertFalse(exists)
            }

            testContext.assertThrow(PersistenceException::class.java){
                withTransaction {repository.delete(it,NotExistsEntity::class.java,0)}.await()
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testQueryList(repository:EntityRepositoryUni,testContext: VertxTestContext){
        testContext.execute {
            val user =  randomUser()
            withTransaction { repository.save(it,user) }.await()

            var list = withTransaction { repository.listQuery(it,User::class.java,"from User") }.await()
            testContext.verify {
                Assertions.assertTrue(list.isNotEmpty())
            }

            list = withTransaction { repository.listQuery(it,User::class.java,"from User where username = :username", mapOf("username" to user.username)) }.await()
            testContext.verify {
                Assertions.assertTrue(list.isNotEmpty())
            }

            list = withTransaction { repository.listQuery(it,User::class.java,"from User where username = :username", mapOf("username" to UUID.randomUUID().toString())) }.await()
            testContext.verify {
                Assertions.assertTrue(list.isEmpty())
            }

            testContext.assertThrow(PersistenceException::class.java){
                withTransaction {repository.listQuery(it,NotExistsEntity::class.java,"from NotExistsEntity where username = :username")}.await()
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testSingleQuery(repository:EntityRepositoryUni,testContext: VertxTestContext){
        testContext.execute {
            val user =  randomUser()
            withTransaction { repository.save(it,user) }.await()

            var query = withTransaction { repository.singleQuery(it,User::class.java,"from User where username = :username", mapOf("username" to user.username)) }.await()
            testContext.verify {
                Assertions.assertNotNull(query)
            }

            query = withTransaction { repository.singleQuery(it,User::class.java,"from User where username = :username", mapOf("username" to UUID.randomUUID().toString())) }.await()
            testContext.verify {
                Assertions.assertNull(query)
            }

            testContext.assertThrow(PersistenceException::class.java){
                withTransaction { repository.singleQuery(it,NotExistsEntity::class.java,"from NotExistsEntity where username = :username") }.await()
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testExecuteUpdate(repository:EntityRepositoryUni,testContext: VertxTestContext){
        testContext.execute {
            val user =  randomUser()
            withTransaction{repository.save(it,user)}.await()

            val updated = withTransaction { repository.executeUpdate(it,"update User set age = :age", mapOf("age" to 40)) }.await()
            testContext.verify {
                Assertions.assertTrue(updated > 0L)
            }

            val queryUser = withTransaction { repository.singleQuery(it,User::class.java,"from User where username = :username", mapOf("username" to user.username)) }.await()
            testContext.verify {
                Assertions.assertNotNull(queryUser)
                Assertions.assertEquals(queryUser!!.age,40)
            }


            testContext.assertThrow(Exception::class.java){
                withTransaction {repository.executeUpdate(it,"update NotExistsEntity set age = :age", mapOf("age" to 40)) }.await()
            }
        }
    }

    private fun createUser(sessionObject: SessionObject,repository: EntityRepositoryUni):Uni<User>{
        val user =  randomUser()
        return repository.save(sessionObject,user)
    }


    private fun randomUser():User {
        return User(username = randomIDString.randomString(),age = Random.nextInt(10,50))
    }

}