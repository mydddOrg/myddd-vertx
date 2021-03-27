package com.foreverht.isvgateway.domain.infra

import com.foreverht.isvgateway.domain.ProxyMediaRepository
import org.myddd.vertx.repository.hibernate.EntityRepositoryHibernate

class ProxyMediaRepositoryHibernate:EntityRepositoryHibernate(),ProxyMediaRepository {
}