package org.myddd.vertx.oauth2.infra.repsitory

import org.myddd.vertx.oauth2.domain.OAuth2TokenRepository
import org.myddd.vertx.repository.hibernate.EntityRepositoryHibernate

class OAuth2TokenRepositoryHibernate : OAuth2TokenRepository, EntityRepositoryHibernate() {
}