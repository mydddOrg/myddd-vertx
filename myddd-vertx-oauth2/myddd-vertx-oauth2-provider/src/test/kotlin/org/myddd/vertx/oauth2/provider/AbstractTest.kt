package org.myddd.vertx.oauth2.provider

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Vertx
import io.vertx.ext.auth.oauth2.OAuth2Auth
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.hibernate.reactive.mutiny.Mutiny
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.id.IDGenerator
import org.myddd.vertx.id.SnowflakeDistributeId
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.oauth2.api.OAuth2Application
import org.myddd.vertx.oauth2.application.OAuth2ApplicationJPA
import org.myddd.vertx.oauth2.domain.OAuth2ClientRepository
import org.myddd.vertx.oauth2.domain.OAuth2ClientService
import org.myddd.vertx.oauth2.domain.OAuth2TokenRepository
import org.myddd.vertx.oauth2.infra.repsitory.OAuth2ClientRepositoryHibernate
import org.myddd.vertx.oauth2.infra.repsitory.OAuth2TokenRepositoryHibernate
import org.myddd.vertx.querychannel.api.QueryChannel
import org.myddd.vertx.querychannel.hibernate.QueryChannelHibernate
import org.myddd.vertx.repository.hibernate.MydddServiceContributor
import org.myddd.vertx.string.RandomIDString
import org.myddd.vertx.string.RandomIDStringProvider
import javax.persistence.Persistence

@ExtendWith(VertxExtension::class)
abstract class AbstractTest {

    companion object {
        private val guiceInstanceProvider by lazy {
            GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
                override fun configure() {
                    val vertx = Vertx.vertx()
                    bind(Vertx::class.java).toInstance(vertx)
                    MydddServiceContributor.vertx = vertx

                    bind(Mutiny.SessionFactory::class.java).toInstance(
                        Persistence.createEntityManagerFactory("default")
                            .unwrap(Mutiny.SessionFactory::class.java))

                    bind(QueryChannel::class.java).to(QueryChannelHibernate::class.java)
                    bind(OAuth2ClientService::class.java)

                    bind(OAuth2ClientRepository::class.java).to((OAuth2ClientRepositoryHibernate::class.java))
                    bind(OAuth2TokenRepository::class.java).to((OAuth2TokenRepositoryHibernate::class.java))
                    bind(OAuth2Application::class.java).to(OAuth2ApplicationJPA::class.java)

                    bind(OAuth2Auth::class.java).to(MydddVertXOAuth2Provider::class.java)
                    bind(RandomIDString::class.java).to(RandomIDStringProvider::class.java)
                    bind(IDGenerator::class.java).toInstance(SnowflakeDistributeId())
                }
            }))
        }

        @BeforeAll
        @JvmStatic
        fun beforeAll(testContext: VertxTestContext){
            InstanceFactory.setInstanceProvider(guiceInstanceProvider)
            testContext.completeNow()
        }
    }

}