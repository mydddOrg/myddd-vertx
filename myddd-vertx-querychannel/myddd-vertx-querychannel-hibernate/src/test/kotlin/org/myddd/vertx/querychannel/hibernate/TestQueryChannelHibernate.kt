package org.myddd.vertx.querychannel.hibernate

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.hibernate.reactive.mutiny.Mutiny
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.querychannel.api.PageQuery
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

    @BeforeEach
    fun beforeEach(){
        val users = ArrayList<User>()
        for (i in 1..10){
            users.add(User(username = "lingen_${i}",age = 35 + i))
        }

        val userArray:Array<User> = users.toTypedArray()
        repository.batchSave(userArray).onSuccess { success ->
            require(success)
        }
    }

    @Test
    fun testPageQuery(vertx: Vertx, testContext: VertxTestContext) {
        queryChannel.pageQuery(PageQuery(clazz = User::class.java,sql = "from User")).onSuccess { pageResult ->
            if(pageResult.totalCount > 0 && pageResult.dataList.isNotEmpty()) testContext.completeNow() else testContext.failNow("分页查询出错")
        }
    }


    @Test
    fun testListQuery(vertx: Vertx, testContext: VertxTestContext){
        queryChannel.queryList(User::class.java,"from User").onSuccess { list ->
            if(list.isNotEmpty()) testContext.completeNow() else testContext.failNow("没有数据")
        }
    }

}