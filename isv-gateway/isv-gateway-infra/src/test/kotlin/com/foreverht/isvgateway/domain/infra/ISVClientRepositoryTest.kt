package com.foreverht.isvgateway.domain.infra

import com.foreverht.isvgateway.AbstractTest
import com.foreverht.isvgateway.domain.ISVClientRepository
import org.myddd.vertx.ioc.InstanceFactory

class ISVClientRepositoryTest : AbstractTest() {

    private val clientRepository by lazy { InstanceFactory.getInstance(ISVClientRepository::class.java) }


}