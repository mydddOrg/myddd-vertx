package com.foreverht.isvgateway.domain

import org.myddd.vertx.domain.BaseEntity
import javax.persistence.*

@Entity
@Table(name = "proxy_employee_organization")
class ProxyEmpOrgRelation: BaseEntity() {

    @Column(name = "auth_code_id")
    var authCode:Long = 0

    @ManyToOne(cascade=[],fetch=FetchType.EAGER)
    @JoinColumn(name = "employee_id")
    lateinit var employee: ProxyEmployee

    @ManyToOne(cascade=[],fetch= FetchType.EAGER)
    @JoinColumn(name = "organization_id")
    lateinit var organization: ProxyOrganization

    var main:Boolean = false

    companion object {
        fun createInstance(employee:ProxyEmployee,organization: ProxyOrganization,isMain: Boolean = false):ProxyEmpOrgRelation{
            val instance = ProxyEmpOrgRelation()
            instance.authCode = employee.authCode.id
            instance.employee = employee
            instance.organization = organization
            instance.main = isMain
            return instance
        }

    }
}