package com.foreverht.isvgateway

import com.foreverht.isvgateway.domain.*
import com.foreverht.isvgateway.domain.infra.ISVClientRepositoryHibernate
import com.foreverht.isvgateway.domain.infra.ProxyMediaRepositoryHibernate
import com.foreverht.isvgateway.domain.infra.ProxyRepositoryHibernate
import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
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
import org.myddd.vertx.string.RandomIDString
import org.myddd.vertx.string.RandomIDStringProvider
import javax.persistence.Persistence

@ExtendWith(VertxExtension::class)
abstract class AbstractTest {

    companion object {

        val logger by lazy { LoggerFactory.getLogger(AbstractTest::class.java) }

        val randomIDString by lazy { InstanceFactory.getInstance(RandomIDString::class.java) }

        @BeforeAll
        @JvmStatic
        fun beforeAll(vertx: Vertx,testContext: VertxTestContext){
            InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
                override fun configure() {
                    bind(Mutiny.SessionFactory::class.java).toInstance(
                        Persistence.createEntityManagerFactory("default")
                            .unwrap(Mutiny.SessionFactory::class.java))

                    bind(OAuth2ClientRepository::class.java).to(OAuth2ClientRepositoryHibernate::class.java)
                    bind(OAuth2ClientService::class.java)
                    bind(OAuth2TokenRepository::class.java).to((OAuth2TokenRepositoryHibernate::class.java))

                    bind(ISVClientRepository::class.java).to(ISVClientRepositoryHibernate::class.java)
                    bind(RandomIDString::class.java).to(RandomIDStringProvider::class.java)

                    bind(ProxyRepository::class.java).to(ProxyRepositoryHibernate::class.java)
                    bind(ProxyMediaRepository::class.java).to(ProxyMediaRepositoryHibernate::class.java)
                }
            })))
            testContext.completeNow()
        }
    }

    fun randomString():String {
        return randomIDString.randomString()
    }

    fun randomISVAuthCode(): ISVAuthCode {
        val isvAuthCode = ISVAuthCode()
        isvAuthCode.suiteId = randomString()
        isvAuthCode.clientType = ISVClientType.WorkPlusISV
        isvAuthCode.authStatus = ISVAuthStatus.Temporary
        isvAuthCode.domainId = randomString()
        isvAuthCode.orgCode = randomString()
        isvAuthCode.temporaryAuthCode = randomString()
        return isvAuthCode
    }

    fun randomEmployee(authCode: ISVAuthCode):ProxyEmployee{
        val employee = ProxyEmployee()
        employee.authCode = authCode
        employee.userId = randomString()
        employee.name = randomString()
        employee.avatar = randomString()
        employee.mobile = randomString()
        employee.email = randomString()
        return employee
    }

    fun randomOrganization(authCode: ISVAuthCode):ProxyOrganization {
        val organization = ProxyOrganization()
        organization.authCode = authCode
        organization.orgId = randomString()
        organization.orgCode = randomString()
        organization.parentOrgId = randomString()
        organization.path = randomString()
        organization.name = randomString()
        return organization
    }
}