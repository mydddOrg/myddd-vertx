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
    suspend fun <T : Entity> save(entity: T): Future<T>

    /**
     * 查询一个实体
     */
    suspend fun <T : Entity> get(clazz: Class<T>?, id: Serializable?): Future<T?>

    /**
     * 实体是否存在
     */
    suspend fun <T : Entity> exists(clazz: Class<T>?, id: Serializable?): Future<Boolean>

    /**
     * 移除一个实体
     */
    suspend fun <T : Entity> remove(entity:T):Future<Unit>

    /**
     * 批量更新
     */
    suspend fun <T : Entity> batchSave(entityList:Array<T>): Future<Boolean>

    /**
     * 删除
     */
    suspend fun <T : Entity> delete(clazz: Class<T>?, id: Serializable?): Future<Boolean>

    /**
     * 执行一个查询，返回一个LIST
     */
    suspend fun <T> listQuery(clazz: Class<T>?,sql:String,params:Map<String,Any> = mapOf()):Future<List<T>>


    /**
     * 执行一个查询，返回单个数值
     */
    suspend fun <T> singleQuery(clazz: Class<T>?, sql: String, params: Map<String, Any> = mapOf()): Future<T?>

    /**
     * 执行一个更新操作
     */
    suspend fun executeUpdate(sql:String,params:Map<String,Any> = mapOf()):Future<Int?>

}