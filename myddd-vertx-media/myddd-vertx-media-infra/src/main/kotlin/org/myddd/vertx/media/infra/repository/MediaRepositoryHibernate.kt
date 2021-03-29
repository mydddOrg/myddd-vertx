package org.myddd.vertx.media.infra.repository

import org.myddd.vertx.media.domain.MediaRepository
import org.myddd.vertx.repository.hibernate.EntityRepositoryHibernate

class MediaRepositoryHibernate:EntityRepositoryHibernate(),MediaRepository {
}