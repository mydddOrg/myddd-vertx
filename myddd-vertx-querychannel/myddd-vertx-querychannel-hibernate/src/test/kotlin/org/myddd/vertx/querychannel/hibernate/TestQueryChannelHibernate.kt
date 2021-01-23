package org.myddd.vertx.querychannel.hibernate

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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.querychannel.api.PageParam
import org.myddd.vertx.querychannel.api.QueryParam
import org.myddd.vertx.repository.hibernate.EntityRepositoryHibernate
import javax.persistence.Persistence

@ExtendWith(VertxExtension::class)
class TestQueryChannelHibernate {

    private var queryChannel = QueryChannelHibernate()
    private val repository = EntityRepositoryHibernate()

    private val sessionFactory: Mutiny.SessionFactory by lazy { Persistence.createEntityManagerFactory("default")
        .unwrap(Mutiny.SessionFactory::class.java) }

    init {
        InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
            override fun configure() {
                bind(Mutiny.SessionFactory::class.java).toInstance(sessionFactory)
            }
        })))
    }

    private suspend fun prepareData(){
        val users = ArrayList<User>()
        for (i in 1..10){
            users.add(User(username = "lingen_${i}",age = 35 + i))
        }

        val userArray:Array<User> = users.toTypedArray()
        val success = repository.batchSave(userArray).await()
        Assertions.assertTrue(success)
    }

    @Test
    fun testPageQuery(vertx: Vertx, testContext: VertxTestContext) {
        GlobalScope.launch {
            try{
                prepareData()

                val pageResult = queryChannel.pageQuery(
                    QueryParam(clazz = User::class.java,sql = "from User where username like :username",params = mapOf("username" to "%lingen%")),
                    PageParam(pageSize = 10)
                ).await()
                Assertions.assertTrue(pageResult.totalCount > 0)
                Assertions.assertTrue(pageResult.dataList.isNotEmpty())
                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }

        }
    }


    @Test
    fun testListQuery(vertx: Vertx, testContext: VertxTestContext){
        GlobalScope.launch {
            prepareData()

            val list = queryChannel.queryList(QueryParam(clazz = User::class.java,sql ="from User")).await()
            Assertions.assertTrue(list.isNotEmpty())
            testContext.completeNow()
        }
    }

}