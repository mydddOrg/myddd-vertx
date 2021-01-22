package org.myddd.vertx.repository.api

import io.vertx.core.Future
import org.myddd.vertx.domain.Entity
import java.io.Serializable


/**
 * 抽像仓储
 */
interface EntityRepository {

    fun <T : Entity?> save(entity: T): Future<T>

    fun <T : Entity?> get(clazz: Class<T>?, id: Serializable?): Future<T?>

    fun <T : Entity?> exists(clazz: Class<T>?, id: Serializable?): Future<Boolean>

}