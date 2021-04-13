package org.myddd.vertx.querychannel.hibernate

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
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.querychannel.api.PageParam
import org.myddd.vertx.querychannel.api.QueryChannel
import org.myddd.vertx.querychannel.api.QueryParam
import org.myddd.vertx.repository.api.EntityRepository
import org.myddd.vertx.repository.hibernate.EntityRepositoryHibernate
import java.util.stream.Stream
import javax.persistence.Persistence

@ExtendWith(VertxExtension::class)
class TestQueryChannelHibernate {

    private val repositories:Array<EntityRepository> = arrayOf(EntityRepositoryHibernate(),EntityRepositoryHibernate(dataSource = "pg"))

    init {
        InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
            override fun configure() {
                bind(Mutiny.SessionFactory::class.java).toInstance(Persistence.createEntityManagerFactory("default")
                    .unwrap(Mutiny.SessionFactory::class.java))

                bind(Mutiny.SessionFactory::class.java).annotatedWith(Names.named("pg")).toInstance(Persistence.createEntityManagerFactory("pg")
                    .unwrap(Mutiny.SessionFactory::class.java))
            }
        })))
    }

    companion object {
        @JvmStatic
        fun parametersQueryChannel():Stream<QueryChannel>{
            return Stream.of(
                QueryChannelHibernate(),
                QueryChannelHibernate(dataSource = "pg")
            )
        }
    }

    @ParameterizedTest
    @MethodSource("parametersQueryChannel")
    fun testPageQuery(queryChannel:QueryChannel,vertx: Vertx,testContext: VertxTestContext) {
        GlobalScope.launch(vertx.dispatcher()) {
            try{
                prepareData(testContext)

                val pageResult = queryChannel.pageQuery(
                    QueryParam(clazz = User::class.java,sql = "from User where username like :username",params = mapOf("username" to "%lingen%")),
                    PageParam(limit = 10)
                ).await()

                testContext.verify {
                    Assertions.assertTrue(pageResult.totalCount > 0)
                    Assertions.assertTrue(pageResult.dataList.isNotEmpty())
                }
                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }

        }
    }


    @ParameterizedTest
    @MethodSource("parametersQueryChannel")
    fun testListQuery(queryChannel:QueryChannel,vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            prepareData(testContext)

            val list = queryChannel.queryList(QueryParam(clazz = User::class.java,sql ="from User")).await()
            testContext.verify {
                Assertions.assertTrue(list.isNotEmpty())
            }
            testContext.completeNow()
        }
    }

    private suspend fun prepareData(testContext: VertxTestContext){

        repositories.forEach { repository ->
            val users = ArrayList<User>()
            for (i in 1..10){
                users.add(User(username = "lingen_${i}",age = 35 + i))
            }

            val userArray:Array<User> = users.toTypedArray()
            val success = repository.batchSave(userArray).await()
            testContext.verify {
                Assertions.assertTrue(success)
            }
        }

    }




}