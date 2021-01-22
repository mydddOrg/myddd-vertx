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

@ExtendWith(VertxExtension::class)
class TestEntityRepositoryHibernate {

    private val entityRepository:EntityRepositoryHibernate = EntityRepositoryHibernate()

    @Test
    fun testAdd(vertx:Vertx, testContext: VertxTestContext){
        val user =  User(username = "lingen",age = 35)
        entityRepository.save(user).onSuccess {
            user -> if(user.id > 0) testContext.completeNow() else testContext.failed()
        }
    }

    @Test
    fun testFind(vertx:Vertx, testContext: VertxTestContext){

        GlobalScope.launch {
            val user =  User(username = "lingen",age = 35)
            val createdUser =  entityRepository.save(user).await()
            var queryUser =entityRepository.get(User::class.java,createdUser.id).await()

            if(queryUser != null)testContext.completeNow() else testContext.failed()
            testContext.completeNow()
        }
    }

    @Test
    fun testExists(vertx:Vertx, testContext: VertxTestContext){
        GlobalScope.launch {
            val user =  User(username = "lingen",age = 35)
            val createdUser =  entityRepository.save(user).await()
            var exists =entityRepository.exists(User::class.java,createdUser.id).await()

            if(exists)testContext.completeNow() else testContext.failed()
        }
    }


}