package com.foreverht.isvgateway.domain.infra

import com.foreverht.isvgateway.domain.ISVClientRepository
import org.myddd.vertx.repository.hibernate.EntityRepositoryHibernate

class ISVClientRepositoryHibernate : EntityRepositoryHibernate(),ISVClientRepository {

}