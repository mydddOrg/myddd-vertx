package org.myddd.vertx.repository.api

import io.vertx.core.Future
import org.myddd.vertx.domain.Entity
import java.io.Serializable


/**
 * 抽像仓储
 */
interface EntityRepository {

    /**
     * 更新一个实体
     */
    fun <T : Entity> save(entity: T): Future<T>

    /**
     * 查询一个实体
     */
    fun <T : Entity> get(clazz: Class<T>?, id: Serializable?): Future<T?>

    /**
     * 实体是否存在
     */
    fun <T : Entity> exists(clazz: Class<T>?, id: Serializable?): Future<Boolean>

    /**
     * 批量更新
     */
    fun <T : Entity> batchSave(entityList:Array<T>): Future<Boolean>

    /**
     * 删除
     */
    fun <T : Entity> delete(clazz: Class<T>?, id: Serializable?): Future<Boolean>

}