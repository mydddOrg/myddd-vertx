package com.foreverht.isvgateway.bootstrap

import com.foreverht.isvgateway.api.*
import com.foreverht.isvgateway.application.ISVClientApplicationImpl
import com.foreverht.isvgateway.application.workplus.*
import com.foreverht.isvgateway.domain.ISVClientRepository
import com.foreverht.isvgateway.domain.infra.ISVClientRepositoryHibernate
import com.google.inject.name.Names
import io.vertx.core.Vertx
import org.myddd.vertx.web.router.AbstractWebModule

class ISVClientGuice(vertx: Vertx) : AbstractWebModule(vertx = vertx) {

    companion object {
        private const val ANNOTATED_WORKPLUS_APP = "WorkPlusApp"
    }
    override fun configure(){
        super.configure()

        bind(ISVClientRepository::class.java).to(ISVClientRepositoryHibernate::class.java)
        bind(ISVClientApplication::class.java).to(ISVClientApplicationImpl::class.java)

        bind(AccessTokenApplication::class.java).annotatedWith(Names.named(ANNOTATED_WORKPLUS_APP)).to(AccessTokenApplicationWorkPlus::class.java)
        bind(OrganizationApplication::class.java).annotatedWith(Names.named(ANNOTATED_WORKPLUS_APP)).to((OrganizationApplicationWorkPlus::class.java))
        bind(EmployeeApplication::class.java).annotatedWith(Names.named(ANNOTATED_WORKPLUS_APP)).to(EmployeeApplicationWorkPlus::class.java)
        bind(MediaApplication::class.java).annotatedWith(Names.named(ANNOTATED_WORKPLUS_APP)).to(MediaApplicationWorkPlus::class.java)
        bind(AppApplication::class.java).annotatedWith(Names.named(ANNOTATED_WORKPLUS_APP)).to(AppApplicationWorkPlus::class.java)
        bind(MessageApplication::class.java).annotatedWith(Names.named(ANNOTATED_WORKPLUS_APP)).to(MessageApplicationWorkPlus::class.java)

    }


}