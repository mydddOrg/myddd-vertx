package org.myddd.vertx.repository.hibernate

import io.smallrye.mutiny.Uni
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.junit.assertNotThrow
import org.myddd.vertx.junit.assertThrow
import org.myddd.vertx.junit.execute
import org.myddd.vertx.junit.randomString
import org.myddd.vertx.repository.api.EntityRepositoryUni
import org.myddd.vertx.repository.hibernate.EntityRepositoryTransaction.withTransaction
import org.myddd.vertx.string.RandomIDString
import java.util.*
import java.util.stream.Stream
import javax.persistence.PersistenceException
import kotlin.random.Random

class TestEntityRepositoryUni:AbstractRepositoryTest() {

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

            withTransaction {
                val createdUserUni = createRandomUser(repository)
                val nextUni = createdUserUni.invoke { created ->
                    testContext.verify {
                        Assertions.assertTrue(created.id > 0)
                    }
                }

                nextUni.chain {created ->
                    created.age = 36
                    repository.saveUni(created)
                }
            }.await()

            testContext.assertThrow(PersistenceException::class.java){
                withTransaction {
                    val errorUser =  User(username = randomIDString.randomString(64),age = 35)
                    repository.saveUni(errorUser)
                }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testRemoveEntity(repository:EntityRepositoryUni,testContext: VertxTestContext){
        testContext.execute {

            testContext.assertThrow(PersistenceException::class.java){
                withTransaction { repository.removeUni(User()) }.await()
            }

            val user =  randomUser()
            val created = withTransaction {
                repository.saveUni(user)
            }.await()

            testContext.assertNotThrow{
                withTransaction { repository.deleteUni(User::class.java,created.id) }.await()
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testUpdate(repository:EntityRepositoryUni,testContext: VertxTestContext){
        testContext.execute {
            val user =  randomUser()
            val createdUser = withTransaction { repository.persistUni(user) }.await()

            val queryUser = withTransaction{ repository.getUni(User::class.java,createdUser.id) }.await()
            testContext.verify {
                Assertions.assertNotNull(queryUser)
            }

            val updatedUser = withTransaction {
                repository.getUni(User::class.java,createdUser.id).chain { user ->
                    user!!.age = 37
                    repository.mergeUni(user)
                }
            }.await()

            testContext.verify {
                Assertions.assertEquals(37,updatedUser.age)
            }

        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testFind(repository:EntityRepositoryUni,testContext: VertxTestContext){

        testContext.execute {
            val user =  randomUser()
            val createdUser = withTransaction { repository.saveUni(user) }.await()
            val queryUser = withTransaction { repository.getUni(User::class.java,createdUser.id)}.await()
            testContext.verify {
                Assertions.assertNotNull(queryUser)
            }
            val notExistsUser = withTransaction {repository.getUni(User::class.java,Long.MAX_VALUE)}.await()
            testContext.verify {
                Assertions.assertFalse(notExistsUser != null)
            }
            testContext.assertThrow(PersistenceException::class.java){
                withTransaction { repository.getUni(NotExistsEntity::class.java,0) }.await()
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testExists(repository: EntityRepositoryUni, testContext: VertxTestContext){
        testContext.execute {
            val user =  randomUser()
            val createdUser = withTransaction { repository.saveUni(user) }.await()
            val exists = withTransaction { repository.existsUni(User::class.java,createdUser.id) }.await()
            testContext.verify {
                Assertions.assertTrue(exists)
            }

            testContext.assertThrow(PersistenceException::class.java){
                withTransaction {repository.existsUni(NotExistsEntity::class.java,0)}.await()
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testBatchAdd(repository:EntityRepositoryUni,testContext: VertxTestContext){
        testContext.execute {
            val users = ArrayList<User>()
            for (i in 1..10){
                users.add(User(username = "lingen_" + randomString(),age = 35 + i))
            }

            val userArray:Array<User> = users.toTypedArray()
            val success = withTransaction { repository.batchSaveUni(userArray) }.await()
            testContext.verify {
                Assertions.assertTrue(success)
            }

            val errorEntities = arrayOf(NotExistsEntity())

            testContext.assertThrow(PersistenceException::class.java){
                withTransaction {repository.batchSaveUni(errorEntities)}.await()
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testDelete(repository:EntityRepositoryUni,testContext: VertxTestContext){
        testContext.execute {
            withTransaction {repository.deleteUni(User::class.java,Long.MAX_VALUE)}.await()

            val user =  randomUser()
            val createdUser = withTransaction {repository.saveUni(user) }.await()

            var exists = withTransaction { repository.existsUni(User::class.java,createdUser.id) }.await()
            testContext.verify { Assertions.assertTrue(exists) }

            withTransaction {repository.deleteUni(User::class.java,createdUser.id) }.await()

            exists = withTransaction { repository.existsUni(User::class.java,createdUser.id) }.await()
            testContext.verify {
                Assertions.assertFalse(exists)
            }

            testContext.assertThrow(PersistenceException::class.java){
                withTransaction {repository.deleteUni(NotExistsEntity::class.java,0)}.await()
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testQueryList(repository:EntityRepositoryUni,testContext: VertxTestContext){
        testContext.execute {
            val user =  randomUser()
            withTransaction { repository.saveUni(user) }.await()

            var list = withTransaction { repository.listQueryUni(User::class.java,"from User") }.await()
            testContext.verify {
                Assertions.assertTrue(list.isNotEmpty())
            }

            list = withTransaction { repository.listQueryUni(User::class.java,"from User where username = :username", mapOf("username" to user.username)) }.await()
            testContext.verify {
                Assertions.assertTrue(list.isNotEmpty())
            }

            list = withTransaction { repository.listQueryUni(User::class.java,"from User where username = :username", mapOf("username" to UUID.randomUUID().toString())) }.await()
            testContext.verify {
                Assertions.assertTrue(list.isEmpty())
            }

            testContext.assertThrow(PersistenceException::class.java){
                withTransaction {repository.listQueryUni(NotExistsEntity::class.java,"from NotExistsEntity where username = :username")}.await()
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testSingleQuery(repository:EntityRepositoryUni,testContext: VertxTestContext){
        testContext.execute {
            val user =  randomUser()
            withTransaction {
                repository.saveUni(user).chain { _ ->
                    repository.singleQueryUni(User::class.java,"from User where username = :username", mapOf("username" to user.username)).invoke { query ->
                        testContext.verify {
                            Assertions.assertNotNull(query)
                        }
                    }.chain { _ ->
                        repository.singleQueryUni(User::class.java,"from User where username = :username", mapOf("username" to UUID.randomUUID().toString())).invoke { query ->
                            testContext.verify {
                                Assertions.assertNull(query)
                            }
                        }
                    }

                }
            }.await()
        }
    }

    @ParameterizedTest
    @MethodSource("parametersRepository")
    fun testExecuteUpdate(repository:EntityRepositoryUni,testContext: VertxTestContext){
        testContext.execute {
            val user =  randomUser()
            withTransaction{repository.saveUni(user)}.await()

            val updated = withTransaction { repository.executeUpdateUni("update User set age = :age", mapOf("age" to 40)) }.await()
            testContext.verify {
                Assertions.assertTrue(updated > 0L)
            }

            val queryUser = withTransaction { repository.singleQueryUni(User::class.java,"from User where username = :username", mapOf("username" to user.username)) }.await()
            testContext.verify {
                Assertions.assertNotNull(queryUser)
                Assertions.assertEquals(queryUser!!.age,40)
            }


            testContext.assertThrow(Exception::class.java){
                withTransaction {repository.executeUpdateUni("update NotExistsEntity set age = :age", mapOf("age" to 40)) }.await()
            }
        }
    }


    private fun randomUser():User {
        return User(username = randomIDString.randomString(),age = Random.nextInt(10,50))
    }

    private fun createRandomUser(repository: EntityRepositoryUni):Uni<User> {
        return repository.persistUni(randomUser())
    }

}