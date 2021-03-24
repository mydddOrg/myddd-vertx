package com.foreverht.isvgateway.domain

import org.myddd.vertx.domain.BaseEntity
import org.myddd.vertx.ioc.InstanceFactory
import javax.persistence.*

@Entity
@Table(name = "proxy_employee_organization")
class ProxyEmpOrgRelation: BaseEntity() {

    @ManyToOne(cascade = [],fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id")
    lateinit var employee: ProxyEmployee

    @ManyToOne(cascade = [],fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id")
    lateinit var organization: ProxyOrganization

    var main:Boolean = true

    companion object {
        private val proxyRepository by lazy { InstanceFactory.getInstance(ProxyRepository::class.java) }

        fun createInstance(employee: ProxyEmployee,organization: ProxyOrganization):ProxyEmpOrgRelation{
            val proxyEmpOrgRelation = ProxyEmpOrgRelation()
            proxyEmpOrgRelation.employee = employee
            proxyEmpOrgRelation.organization = organization
            return proxyEmpOrgRelation
        }
    }

}