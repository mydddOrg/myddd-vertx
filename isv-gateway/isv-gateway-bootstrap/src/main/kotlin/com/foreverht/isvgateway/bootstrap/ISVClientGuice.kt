package com.foreverht.isvgateway.bootstrap

import com.foreverht.isvgateway.api.*
import com.foreverht.isvgateway.application.*
import com.foreverht.isvgateway.application.isv.W6SBossApplicationImpl
import com.foreverht.isvgateway.application.weixin.WeiXinSyncDataApplication
import com.foreverht.isvgateway.application.weixin.WorkWeiXinApplicationImpl
import com.foreverht.isvgateway.application.workplus.*
import com.foreverht.isvgateway.domain.ISVClientRepository
import com.foreverht.isvgateway.domain.ProxyMediaRepository
import com.foreverht.isvgateway.domain.ProxyRepository
import com.foreverht.isvgateway.domain.infra.ISVClientRepositoryHibernate
import com.foreverht.isvgateway.domain.infra.ProxyMediaRepositoryHibernate
import com.foreverht.isvgateway.domain.infra.ProxyRepositoryHibernate
import com.google.inject.name.Names
import io.vertx.core.Vertx

class ISVClientGuice(vertx: Vertx) : AbstractWebModule(vertx = vertx) {

    companion object {
        private const val ANNOTATED_WORKPLUS_APP = "WorkPlusApp"
    }
    override fun configure(){
        super.configure()

        bind(ISVClientApplication::class.java).to(ISVClientApplicationImpl::class.java)
        bind(ISVSuiteTicketApplication::class.java).to(ISVSuiteTicketApplicationImpl::class.java)
        bind(ISVAuthCodeApplication::class.java).to(ISVAuthCodeApplicationImpl::class.java)
        bind(W6SBossApplication::class.java).to(W6SBossApplicationImpl::class.java)
        bind(WorkWeiXinApplication::class.java).to(WorkWeiXinApplicationImpl::class.java)

        bind(AccessTokenApplication::class.java).to(AccessTokenApplicationImpl::class.java)
        bind(OrganizationApplication::class.java).annotatedWith(Names.named(ANNOTATED_WORKPLUS_APP)).to((OrganizationApplicationWorkPlus::class.java))
        bind(EmployeeApplication::class.java).annotatedWith(Names.named(ANNOTATED_WORKPLUS_APP)).to(EmployeeApplicationWorkPlus::class.java)
        bind(MediaApplication::class.java).annotatedWith(Names.named(ANNOTATED_WORKPLUS_APP)).to(MediaApplicationWorkPlus::class.java)
        bind(AppApplication::class.java).annotatedWith(Names.named(ANNOTATED_WORKPLUS_APP)).to(AppApplicationWorkPlus::class.java)
        bind(MessageApplication::class.java).annotatedWith(Names.named(ANNOTATED_WORKPLUS_APP)).to(MessageApplicationWorkPlus::class.java)

        bind(SyncDataApplication::class.java).to(SyncDataApplicationImpl::class.java)
        bind(WeiXinSyncDataApplication::class.java)

        bind(ISVClientRepository::class.java).to(ISVClientRepositoryHibernate::class.java)
        bind(ProxyRepository::class.java).to(ProxyRepositoryHibernate::class.java)
        bind(ProxyMediaRepository::class.java).to(ProxyMediaRepositoryHibernate::class.java)
    }


}