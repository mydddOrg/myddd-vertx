package com.foreverht.isvgateway

import com.foreverht.isvgateway.api.*
import com.foreverht.isvgateway.application.ISVAuthCodeApplicationImpl
import com.foreverht.isvgateway.application.ISVClientApplicationImpl
import com.foreverht.isvgateway.application.ISVSuiteTicketApplicationImpl
import com.foreverht.isvgateway.application.W6SBossApplication
import com.foreverht.isvgateway.application.isv.W6SBossApplicationImpl
import com.foreverht.isvgateway.application.workplus.*
import com.foreverht.isvgateway.domain.ISVClientRepository
import com.foreverht.isvgateway.domain.infra.ISVClientRepositoryHibernate
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.name.Names
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
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

        init {
            InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
                override fun configure() {
                    bind(Vertx::class.java).toInstance(Vertx.vertx())
                    bind(WebClient::class.java).toInstance(WebClient.create(Vertx.vertx()))
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
                    bind(ISVSuiteTicketApplication::class.java).to(ISVSuiteTicketApplicationImpl::class.java)
                    bind(ISVAuthCodeApplication::class.java).to(ISVAuthCodeApplicationImpl::class.java)
                    bind(W6SBossApplication::class.java).to(W6SBossApplicationImpl::class.java)

                    bind(AccessTokenApplication::class.java).annotatedWith(Names.named("WorkPlusApp")).to(AccessTokenApplicationWorkPlus::class.java)
                    bind(OrganizationApplication::class.java).annotatedWith(Names.named("WorkPlusApp")).to((OrganizationApplicationWorkPlus::class.java))
                    bind(EmployeeApplication::class.java).annotatedWith(Names.named("WorkPlusApp")).to(EmployeeApplicationWorkPlus::class.java)
                    bind(MediaApplication::class.java).annotatedWith(Names.named("WorkPlusApp")).to(MediaApplicationWorkPlus::class.java)
                    bind(AppApplication::class.java).annotatedWith(Names.named("WorkPlusApp")).to(AppApplicationWorkPlus::class.java)
                    bind(MessageApplication::class.java).annotatedWith(Names.named("WorkPlusApp")).to(MessageApplicationWorkPlus::class.java)

                }
            })))
        }


    }

    fun randomString():String {
        return randomIDString.randomString()
    }
}