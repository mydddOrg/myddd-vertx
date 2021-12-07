package org.myddd.vertx.querychannel.hibernate

import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.myddd.vertx.junit.execute
import org.myddd.vertx.querychannel.api.PageParam
import org.myddd.vertx.querychannel.api.QueryChannel
import org.myddd.vertx.querychannel.api.QueryParam
import org.myddd.vertx.repository.api.EntityRepository
import org.myddd.vertx.repository.hibernate.EntityRepositoryHibernate
import java.util.stream.Stream

@ExtendWith(VertxExtension::class,IOCInitExtension::class)
class TestQueryChannelHibernate {

    private val repositories:Array<EntityRepository> = arrayOf(EntityRepositoryHibernate())

    companion object {
        @JvmStatic
        fun parametersQueryChannel():Stream<QueryChannel>{
            return Stream.of(
                QueryChannelHibernate(),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("parametersQueryChannel")
    fun testPageQuery(queryChannel:QueryChannel,testContext: VertxTestContext) {
        testContext.execute {
            val pageResult = queryChannel.pageQuery(
                QueryParam(clazz = User::class.java,sql = "from User where username like :username",params = mapOf("username" to "%lingen%")),
                PageParam(limit = 10)
            ).await()

            testContext.verify {
                Assertions.assertTrue(pageResult.totalCount > 0)
                Assertions.assertTrue(pageResult.dataList.isNotEmpty())
            }
        }
    }


    @ParameterizedTest
    @MethodSource("parametersQueryChannel")
    fun testListQuery(queryChannel:QueryChannel,testContext: VertxTestContext){
        testContext.execute {
            val list = queryChannel.queryList(QueryParam(clazz = User::class.java,sql ="from User")).await()
            testContext.verify {
                Assertions.assertTrue(list.isNotEmpty())
            }
        }
    }

    @ParameterizedTest
    @MethodSource("parametersQueryChannel")
    fun testLimitQueryList(queryChannel: QueryChannel,testContext: VertxTestContext){
        testContext.execute {
            val limitQuery = queryChannel.limitQueryList(QueryParam(clazz = User::class.java,sql ="from User"),5).await()
            testContext.verify {
                Assertions.assertTrue(limitQuery.isNotEmpty())
                Assertions.assertEquals(5,limitQuery.size)
            }
        }
    }

    @BeforeEach
    fun beforeEach(testContext: VertxTestContext){
        testContext.execute {
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

}