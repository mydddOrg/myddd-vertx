package com.foreverht.isvgateway

import com.foreverht.isvgateway.api.AccessTokenApplication
import com.foreverht.isvgateway.api.ISVClientApplication
import com.foreverht.isvgateway.application.ISVClientApplicationImpl
import com.foreverht.isvgateway.application.workplus.WorkPlusAccessTokenApplication
import com.foreverht.isvgateway.domain.ISVClientRepository
import com.foreverht.isvgateway.domain.infra.ISVClientRepositoryHibernate
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.name.Names
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.hibernate.reactive.mutiny.Mutiny
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.oauth2.domain.OAuth2ClientRepository
import org.myddd.vertx.oauth2.domain.OAuth2ClientService
import org.myddd.vertx.oauth2.domain.OAuth2TokenRepository
import org.myddd.vertx.oauth2.infra.repsitory.OAuth2ClientRepositoryHibernate
import org.myddd.vertx.oauth2.infra.repsitory.OAuth2TokenRepositoryHibernate
import org.myddd.vertx.querychannel.api.QueryChannel
import org.myddd.vertx.querychannel.hibernate.QueryChannelHibernate
import org.myddd.vertx.string.RandomIDString
import org.myddd.vertx.string.RandomIDStringProvider
import javax.persistence.Persistence

@ExtendWith(VertxExtension::class)
abstract class AbstractTest {

    companion object {

        val randomIDString by lazy { InstanceFactory.getInstance(RandomIDString::class.java) }

        @BeforeAll
        @JvmStatic
        fun beforeAll(vertx: Vertx,testContext: VertxTestContext){
            InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
                override fun configure() {
                    bind(Vertx::class.java).toInstance(vertx)
                    bind(Mutiny.SessionFactory::class.java).toInstance(
                        Persistence.createEntityManagerFactory("default")
                            .unwrap(Mutiny.SessionFactory::class.java))

                    bind(QueryChannel::class.java).to(QueryChannelHibernate::class.java)
                    bind(OAuth2ClientService::class.java)
                    bind(OAuth2ClientRepository::class.java).to((OAuth2ClientRepositoryHibernate::class.java))
                    bind(OAuth2TokenRepository::class.java).to((OAuth2TokenRepositoryHibernate::class.java))

                    bind(RandomIDString::class.java).to(RandomIDStringProvider::class.java)


                    bind(ISVClientRepository::class.java).to(ISVClientRepositoryHibernate::class.java)
                    bind(ISVClientApplication::class.java).to(ISVClientApplicationImpl::class.java)

                    bind(AccessTokenApplication::class.java)
                        .annotatedWith(Names.named("WorkPlus_App"))
                        .to(WorkPlusAccessTokenApplication::class.java)

                }
            })))

            testContext.completeNow()
        }
    }
}