package org.myddd.vertx.repository.api

import io.smallrye.mutiny.Uni
import io.vertx.core.Future
import org.myddd.vertx.domain.Entity
import java.io.Serializable

interface EntityRepositoryUni {

    fun <T : Entity> persistUni(entity: T): Uni<T>

    fun <T : Entity> mergeUni(entity: T): Uni<T>

    fun <T : Entity> saveUni(entity: T): Uni<T>

    fun <T : Entity> existsUni(clazz: Class<T>, id: Serializable): Uni<Boolean>

    fun <T : Entity> removeUni(entity:T):Uni<Unit>

    fun <T : Entity> getUni(clazz: Class<T>, id: Serializable): Uni<T?>

    fun <T : Entity> batchSaveUni(entityList:Array<T>): Uni<Boolean>

    fun <T : Entity> deleteUni(clazz: Class<T>?, id: Serializable?): Uni<Boolean>

    fun <T> singleQueryUni(clazz: Class<T>?, sql:String, params:Map<String,Any> = HashMap()):Uni<T?>

    fun <T> listQueryUni(clazz: Class<T>?, sql:String, params:Map<String,Any> = HashMap()):Uni<List<T>>

    fun executeUpdateUni(sql:String, params:Map<String,Any> = HashMap()):Uni<Int>

    fun <T> withTransaction(execution: () -> Uni<T>): Future<T>

}