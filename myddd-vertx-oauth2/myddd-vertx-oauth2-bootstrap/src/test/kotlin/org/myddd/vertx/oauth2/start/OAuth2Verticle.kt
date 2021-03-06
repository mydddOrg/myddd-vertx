package org.myddd.vertx.oauth2.start

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.coroutines.await
import org.hibernate.reactive.mutiny.Mutiny
import org.myddd.vertx.i18n.I18N
import org.myddd.vertx.i18n.provider.I18NVertxProvider
import org.myddd.vertx.id.IDGenerator
import org.myddd.vertx.id.SnowflakeDistributeId
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.oauth2.api.OAuth2Application
import org.myddd.vertx.oauth2.api.OAuth2ClientApplication
import org.myddd.vertx.oauth2.application.OAuth2ApplicationJPA
import org.myddd.vertx.oauth2.application.OAuth2ClientApplicationJPA
import org.myddd.vertx.oauth2.domain.OAuth2ClientRepository
import org.myddd.vertx.oauth2.domain.OAuth2ClientService
import org.myddd.vertx.oauth2.domain.OAuth2TokenRepository
import org.myddd.vertx.oauth2.infra.repsitory.OAuth2ClientRepositoryHibernate
import org.myddd.vertx.oauth2.infra.repsitory.OAuth2TokenRepositoryHibernate
import org.myddd.vertx.oauth2.start.router.OAuth2ClientRouter
import org.myddd.vertx.oauth2.start.router.OAuth2TokenRouter
import org.myddd.vertx.querychannel.api.QueryChannel
import org.myddd.vertx.querychannel.hibernate.QueryChannelHibernate
import org.myddd.vertx.repository.api.EntityRepository
import org.myddd.vertx.repository.hibernate.EntityRepositoryHibernate
import org.myddd.vertx.string.RandomIDString
import org.myddd.vertx.string.RandomIDStringProvider
import org.myddd.vertx.web.router.BootstrapVerticle
import javax.persistence.Persistence

class OAuth2Verticle(private val port:Int = 8080) : BootstrapVerticle() {

    companion object {
        private val guiceInstanceProvider by lazy {
            GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
                override fun configure(){
                    val vertx = Vertx.vertx()

                    bind(Vertx::class.java).toInstance(vertx)
                    bind(WebClient::class.java).toInstance(WebClient.create(vertx))

                    bind(Mutiny.SessionFactory::class.java).toInstance(
                        Persistence.createEntityManagerFactory("default")
                            .unwrap(Mutiny.SessionFactory::class.java))

                    bind(EntityRepository::class.java).to(EntityRepositoryHibernate::class.java)
                    bind(QueryChannel::class.java).to(QueryChannelHibernate::class.java)

                    bind(OAuth2ClientService::class.java)
                    bind(OAuth2ClientRepository::class.java).to((OAuth2ClientRepositoryHibernate::class.java))
                    bind(OAuth2TokenRepository::class.java).to((OAuth2TokenRepositoryHibernate::class.java))
                    bind(OAuth2Application::class.java).to(OAuth2ApplicationJPA::class.java)

                    bind(OAuth2ClientApplication::class.java).to(OAuth2ClientApplicationJPA::class.java)

                    bind(I18N::class.java).to(I18NVertxProvider::class.java)

                    bind(RandomIDString::class.java).to(RandomIDStringProvider::class.java)
                    bind(IDGenerator::class.java).toInstance(SnowflakeDistributeId())

                }
            }))
        }
    }

    override suspend fun initIOC(vertx: Vertx) {
        vertx.executeBlocking<Unit> {
            InstanceFactory.setInstanceProvider(guiceInstanceProvider)
            it.complete()
        }.await()
    }

    override fun routers(vertx: Vertx, router: Router): () -> Unit {
        return {
            OAuth2ClientRouter(router = router,vertx = vertx, coroutineScope = this)
            OAuth2TokenRouter(router = router,vertx = vertx, coroutineScope = this)
        }
    }
}