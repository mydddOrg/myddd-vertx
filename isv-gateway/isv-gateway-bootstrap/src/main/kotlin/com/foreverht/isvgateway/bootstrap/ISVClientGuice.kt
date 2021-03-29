package com.foreverht.isvgateway.bootstrap

import com.foreverht.isvgateway.api.*
import com.foreverht.isvgateway.application.*
import com.foreverht.isvgateway.application.isv.W6SBossApplicationImpl
import com.foreverht.isvgateway.application.weixin.*
import com.foreverht.isvgateway.application.workplus.*
import com.foreverht.isvgateway.domain.ISVClientRepository
import com.foreverht.isvgateway.domain.ProxyRepository
import com.foreverht.isvgateway.domain.infra.ISVClientRepositoryHibernate
import com.foreverht.isvgateway.domain.infra.ProxyRepositoryHibernate
import com.google.inject.name.Names
import io.vertx.core.Vertx
import org.myddd.vertx.media.domain.MediaRepository
import org.myddd.vertx.media.domain.MediaStorage
import org.myddd.vertx.media.infra.repository.MediaRepositoryHibernate
import org.myddd.vertx.media.storeage.LocalMediaStorage

class ISVClientGuice(vertx: Vertx) : AbstractWebModule(vertx = vertx) {

    companion object {
        private const val WORKPLUS_APP = "WorkPlusApp"
        private const val WORK_WEI_XIN = "WorkWeiXin"
    }
    override fun configure(){
        super.configure()

        bind(ISVClientRepository::class.java).to(ISVClientRepositoryHibernate::class.java)
        bind(ProxyRepository::class.java).to(ProxyRepositoryHibernate::class.java)
        bind(MediaRepository::class.java).to(MediaRepositoryHibernate::class.java)

        bind(ISVClientApplication::class.java).to(ISVClientApplicationImpl::class.java)
        bind(ISVSuiteTicketApplication::class.java).to(ISVSuiteTicketApplicationImpl::class.java)
        bind(ISVAuthCodeApplication::class.java).to(ISVAuthCodeApplicationImpl::class.java)
        bind(W6SBossApplication::class.java).to(W6SBossApplicationImpl::class.java)
        bind(WorkWeiXinApplication::class.java).to(WorkWeiXinApplicationImpl::class.java)
        bind(AccessTokenApplication::class.java).to(AccessTokenApplicationImpl::class.java)
        bind(SyncDataApplication::class.java).to(SyncDataApplicationImpl::class.java)
        bind(WeiXinSyncDataApplication::class.java)

        bindWorkPlus()
        bindWorkWeiXin()

        bind(MediaStorage::class.java).to(LocalMediaStorage::class.java)
    }

    private fun bindWorkPlus(){
        bind(OrganizationApplication::class.java).annotatedWith(Names.named(WORKPLUS_APP)).to((OrganizationApplicationWorkPlus::class.java))
        bind(EmployeeApplication::class.java).annotatedWith(Names.named(WORKPLUS_APP)).to(EmployeeApplicationWorkPlus::class.java)
        bind(MediaApplication::class.java).annotatedWith(Names.named(WORKPLUS_APP)).to(MediaApplicationWorkPlus::class.java)
        bind(AppApplication::class.java).annotatedWith(Names.named(WORKPLUS_APP)).to(AppApplicationWorkPlus::class.java)
        bind(MessageApplication::class.java).annotatedWith(Names.named(WORKPLUS_APP)).to(MessageApplicationWorkPlus::class.java)
    }

    private fun bindWorkWeiXin(){
        bind(AppApplication::class.java).annotatedWith(Names.named(WORK_WEI_XIN)).to(AppApplicationWorkWeiXin::class.java)
        bind(OrganizationApplication::class.java).annotatedWith(Names.named(WORK_WEI_XIN)).to(
            OrganizationApplicationWorkWeiXin::class.java)
        bind(EmployeeApplication::class.java).annotatedWith(Names.named(WORK_WEI_XIN)).to(EmployeeApplicationWorkWeiXin::class.java)
        bind(MessageApplication::class.java).annotatedWith(Names.named(WORK_WEI_XIN)).to(MessageApplicationWorkWeiXin::class.java)
        bind(MediaApplication::class.java).annotatedWith(Names.named(WORK_WEI_XIN)).to(MediaApplicationWorkWeiXin::class.java)
    }
}