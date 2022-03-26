package org.myddd.vertx.repository.api

import io.smallrye.mutiny.Uni
import io.vertx.core.Future
import org.myddd.vertx.domain.Entity
import java.io.Serializable

interface EntityRepositoryUni {

    fun <T : Entity> persist(entity: T): Uni<T>

    fun <T : Entity> merge(entity: T): Uni<T>

    fun <T : Entity> save(entity: T): Uni<T>

    fun <T : Entity> exists(clazz: Class<T>, id: Serializable): Uni<Boolean>

    fun <T : Entity> remove(entity:T):Uni<Unit>

    fun <T : Entity> get(clazz: Class<T>, id: Serializable): Uni<T?>

    fun <T : Entity> batchSave(entityList:Array<T>): Uni<Boolean>

    fun <T : Entity> delete(clazz: Class<T>?, id: Serializable?): Uni<Boolean>

    fun <T> singleQuery(clazz: Class<T>?,sql:String,params:Map<String,Any> = HashMap()):Uni<T?>

    fun <T> listQuery(clazz: Class<T>?,sql:String,params:Map<String,Any> = HashMap()):Uni<List<T>>

    fun executeUpdate(sql:String,params:Map<String,Any> = HashMap()):Uni<Int>

    fun <T> withTransaction(execution: () -> Uni<T>): Future<T>

}