package com.foreverht.isvgateway.domain

import org.myddd.vertx.domain.BaseEntity
import org.myddd.vertx.ioc.InstanceFactory
import javax.persistence.*

@Entity
@Table(name = "proxy_organization",
    indexes = [
        Index(name = "index_auth_code_id",columnList = "auth_code_id"),
        Index(name = "index_org_id",columnList = "org_id"),
        Index(name = "index_parent_org_id",columnList = "parent_org_id")
    ],
    uniqueConstraints = [UniqueConstraint(columnNames = ["org_id"])]
)
class ProxyOrganization: BaseEntity() {

    @ManyToOne(cascade = [],fetch = FetchType.EAGER)
    @JoinColumn(name = "auth_code_id")
    lateinit var authCode:ISVAuthCode

    @Column(name = "org_id")
    lateinit var orgId:String

    @Column(name = "parent_org_id")
    lateinit var parentOrgId:String

    lateinit var orgCode:String

    lateinit var path:String

    companion object {
        private val proxyRepository by lazy { InstanceFactory.getInstance(ProxyRepository::class.java) }
    }

}