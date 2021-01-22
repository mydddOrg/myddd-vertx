package org.myddd.vertx.repository.hibernate

import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.lang.Exception

@ExtendWith(VertxExtension::class)
class TestEntityRepositoryHibernate {

    private val repository:EntityRepositoryHibernate = EntityRepositoryHibernate()

    @Test
    fun testAdd(vertx:Vertx, testContext: VertxTestContext){
        val user =  User(username = "lingen",age = 35)
        repository.save(user).onSuccess {
            user -> if(user.id > 0) testContext.completeNow() else testContext.failed()
        }
    }

    @Test
    fun testUpdate(vertx:Vertx, testContext: VertxTestContext){
        GlobalScope.launch {
            val user =  User(username = "lingen",age = 35)
            val createdUser =  repository.save(user).await()
            createdUser.age = 36

            repository.save(createdUser).await()

            var queryUser = repository.get(User::class.java,createdUser.id).await()
            if(queryUser?.age == 36) testContext.completeNow() else testContext.failed()
        }
    }

    @Test
    fun testFind(vertx:Vertx, testContext: VertxTestContext){

        GlobalScope.launch {
            val user =  User(username = "lingen",age = 35)
            val createdUser =  repository.save(user).await()
            var queryUser = repository.get(User::class.java,createdUser.id).await()

            if(queryUser == null)testContext.failed()

            var notExistsUser = repository.get(User::class.java,Long.MAX_VALUE).await()
            if(notExistsUser != null)testContext.failed() else testContext.completeNow()
        }
    }

    @Test
    fun testExists(vertx:Vertx, testContext: VertxTestContext){
        GlobalScope.launch {
            val user =  User(username = "lingen",age = 35)
            val createdUser =  repository.save(user).await()
            var exists =repository.exists(User::class.java,createdUser.id).await()

            if(exists)testContext.completeNow() else testContext.failed()
        }
    }

    @Test
    fun testBatchAdd(vertx:Vertx, testContext: VertxTestContext){
        val users = ArrayList<User>()
        for (i in 1..10){
            users.add(User(username = "lingen_${i}",age = 35 + i))
        }

        val userArray:Array<User> = users.toTypedArray()
        repository.batchSave(userArray).onSuccess { success -> if (success) testContext.completeNow() else testContext.failed() }
    }

    @Test
    fun testDelete(vertx:Vertx, testContext: VertxTestContext){
        GlobalScope.launch {
            try {
                repository.delete(User::class.java,Long.MAX_VALUE).onFailure { exception -> println(exception) }.await()
            }catch (e:Exception){

            }


            val user =  User(username = "lingen",age = 35)
            val createdUser =  repository.save(user).await()
            var exists = repository.exists(User::class.java,createdUser.id).await()
            if(!exists)testContext.failed()

            repository.delete(User::class.java,createdUser.id).await()
            exists = repository.exists(User::class.java,createdUser.id).await()
            if(!exists)testContext.completeNow() else testContext.failed()

        }
    }

}