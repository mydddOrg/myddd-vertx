package org.myddd.vertx.oauth2.infra.repsitory

import org.myddd.vertx.oauth2.domain.OAuth2ClientRepository
import org.myddd.vertx.repository.hibernate.EntityRepositoryHibernate

class OAuth2ClientRepositoryHibernate :EntityRepositoryHibernate(), OAuth2ClientRepository{

}