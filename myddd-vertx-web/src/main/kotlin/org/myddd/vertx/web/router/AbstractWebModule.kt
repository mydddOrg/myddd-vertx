package org.myddd.vertx.web.router

import com.google.inject.AbstractModule
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import org.hibernate.reactive.mutiny.Mutiny
import org.myddd.vertx.i18n.I18N
import org.myddd.vertx.i18n.provider.I18NVertxProvider
import org.myddd.vertx.oauth2.api.OAuth2Application
import org.myddd.vertx.oauth2.api.OAuth2ClientApplication
import org.myddd.vertx.oauth2.application.OAuth2ApplicationJPA
import org.myddd.vertx.oauth2.application.OAuth2ClientApplicationJPA
import org.myddd.vertx.oauth2.domain.OAuth2ClientRepository
import org.myddd.vertx.oauth2.domain.OAuth2ClientService
import org.myddd.vertx.oauth2.domain.OAuth2TokenRepository
import org.myddd.vertx.oauth2.infra.repsitory.OAuth2ClientRepositoryHibernate
import org.myddd.vertx.oauth2.infra.repsitory.OAuth2TokenRepositoryHibernate
import org.myddd.vertx.querychannel.api.QueryChannel
import org.myddd.vertx.querychannel.hibernate.QueryChannelHibernate
import org.myddd.vertx.repository.api.EntityRepository
import org.myddd.vertx.repository.hibernate.EntityRepositoryHibernate
import org.myddd.vertx.string.RandomIDString
import org.myddd.vertx.string.RandomIDStringProvider
import org.myddd.vertx.web.router.config.GlobalConfig
import javax.persistence.Persistence

abstract class AbstractWebModule(private val vertx: Vertx) : AbstractModule() {


    override fun configure(){
        bind(Vertx::class.java).toInstance(vertx)
        bind(WebClient::class.java).toInstance(WebClient.create(vertx))
        bind(Mutiny.SessionFactory::class.java).toInstance(
            Persistence.createEntityManagerFactory("default",persistenceProps())
                .unwrap(Mutiny.SessionFactory::class.java))
        bind(EntityRepository::class.java).to(EntityRepositoryHibernate::class.java)
        bind(QueryChannel::class.java).to(QueryChannelHibernate::class.java)

        bind(OAuth2ClientService::class.java)
        bind(OAuth2ClientRepository::class.java).to((OAuth2ClientRepositoryHibernate::class.java))
        bind(OAuth2TokenRepository::class.java).to((OAuth2TokenRepositoryHibernate::class.java))

        bind(RandomIDString::class.java).to(RandomIDStringProvider::class.java)

        bind(OAuth2Application::class.java).to(OAuth2ApplicationJPA::class.java)
        bind(OAuth2ClientApplication::class.java).to(OAuth2ClientApplicationJPA::class.java)
        bind(I18N::class.java).to(I18NVertxProvider::class.java)
    }


    private fun persistenceProps():Map<String,Any>{
        return mapOf(
            "javax.persistence.jdbc.url" to GlobalConfig.getConfig()!!.getString("javax.persistence.jdbc.url"),
            "javax.persistence.jdbc.user" to GlobalConfig.getConfig()!!.getString("javax.persistence.jdbc.user"),
            "javax.persistence.jdbc.password" to GlobalConfig.getConfig()!!.getString("javax.persistence.jdbc.password"),
            "hibernate.connection.pool_size" to GlobalConfig.getConfig()!!.getInteger("hibernate.connection.pool_size"),
            "javax.persistence.schema-generation.database.action" to GlobalConfig.getConfig()!!.getString("javax.persistence.schema-generation.database.action"),
            "hibernate.show_sql" to GlobalConfig.getConfig()!!.getBoolean("hibernate.show_sql",false),
            "hibernate.format_sql" to GlobalConfig.getConfig()!!.getBoolean("hibernate.format_sql",false),
            "hibernate.highlight_sql" to GlobalConfig.getConfig()!!.getBoolean("hibernate.highlight_sql",false)
        )
    }
}