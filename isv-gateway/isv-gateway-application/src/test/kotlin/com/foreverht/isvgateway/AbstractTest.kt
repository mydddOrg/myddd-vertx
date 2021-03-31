package com.foreverht.isvgateway

import com.foreverht.isvgateway.api.*
import com.foreverht.isvgateway.application.*
import com.foreverht.isvgateway.application.isv.W6SBossApplicationImpl
import com.foreverht.isvgateway.application.weixin.*
import com.foreverht.isvgateway.application.workplus.*
import com.foreverht.isvgateway.domain.ISVClientRepository
import com.foreverht.isvgateway.domain.ProxyRepository
import com.foreverht.isvgateway.domain.infra.ISVClientRepositoryHibernate
import com.foreverht.isvgateway.domain.infra.ProxyRepositoryHibernate
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.name.Names
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxExtension
import org.hibernate.reactive.mutiny.Mutiny
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.file.FileDigest
import org.myddd.vertx.file.FileDigestProvider
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider
import org.myddd.vertx.media.domain.MediaRepository
import org.myddd.vertx.media.domain.MediaStorage
import org.myddd.vertx.media.infra.repository.MediaRepositoryHibernate
import org.myddd.vertx.media.storeage.LocalMediaStorage
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

        private const val WORK_WEI_XIN = "WorkWeiXin"
        private const val WORKPLUS_APP = "WorkPlusApp"


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
                    bind(FileDigest::class.java).to(FileDigestProvider::class.java)


                    bind(ISVClientRepository::class.java).to(ISVClientRepositoryHibernate::class.java)
                    bind(ISVClientApplication::class.java).to(ISVClientApplicationImpl::class.java)
                    bind(ISVSuiteTicketApplication::class.java).to(ISVSuiteTicketApplicationImpl::class.java)
                    bind(ISVAuthCodeApplication::class.java).to(ISVAuthCodeApplicationImpl::class.java)
                    bind(W6SBossApplication::class.java).to(W6SBossApplicationImpl::class.java)
                    bind(WorkWeiXinApplication::class.java).to(WorkWeiXinApplicationImpl::class.java)

                    bind(AccessTokenApplication::class.java).to(AccessTokenApplicationImpl::class.java)

                    bind(OrganizationApplication::class.java).annotatedWith(Names.named(WORKPLUS_APP)).to((OrganizationApplicationWorkPlus::class.java))
                    bind(EmployeeApplication::class.java).annotatedWith(Names.named(WORKPLUS_APP)).to(EmployeeApplicationWorkPlus::class.java)
                    bind(MediaApplication::class.java).annotatedWith(Names.named(WORKPLUS_APP)).to(MediaApplicationWorkPlus::class.java)
                    bind(AppApplication::class.java).annotatedWith(Names.named(WORKPLUS_APP)).to(AppApplicationWorkPlus::class.java)
                    bind(MessageApplication::class.java).annotatedWith(Names.named(WORKPLUS_APP)).to(MessageApplicationWorkPlus::class.java)

                    bind(AppApplication::class.java).annotatedWith(Names.named(WORK_WEI_XIN)).to(AppApplicationWorkWeiXin::class.java)
                    bind(OrganizationApplication::class.java).annotatedWith(Names.named(WORK_WEI_XIN)).to(OrganizationApplicationWorkWeiXin::class.java)
                    bind(EmployeeApplication::class.java).annotatedWith(Names.named(WORK_WEI_XIN)).to(EmployeeApplicationWorkWeiXin::class.java)
                    bind(MessageApplication::class.java).annotatedWith(Names.named(WORK_WEI_XIN)).to(MessageApplicationWorkWeiXin::class.java)
                    bind(MediaApplication::class.java).annotatedWith(Names.named(WORK_WEI_XIN)).to(MediaApplicationWorkWeiXin::class.java)

                    bind(ProxyRepository::class.java).to(ProxyRepositoryHibernate::class.java)
                    bind(SyncDataApplication::class.java).to(SyncDataApplicationImpl::class.java)
                    bind(WeiXinSyncDataApplication::class.java)

                    bind(MediaStorage::class.java).to(LocalMediaStorage::class.java)
                    bind(MediaRepository::class.java).to(MediaRepositoryHibernate::class.java)

                }
            })))
        }


    }

    fun randomString():String {
        return randomIDString.randomString()
    }
}