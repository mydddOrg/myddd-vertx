package com.foreverht.isvgateway.domain.infra

import com.foreverht.isvgateway.domain.ISVClient
import com.foreverht.isvgateway.domain.ISVClientRepository
import io.vertx.core.Future
import org.myddd.vertx.repository.hibernate.EntityRepositoryHibernate

class ISVClientRepositoryHibernate : EntityRepositoryHibernate(),ISVClientRepository {

}