package org.myddd.vertx.repository.api

import io.smallrye.mutiny.Uni
import io.vertx.core.Future
import org.myddd.vertx.domain.Entity
import java.io.Serializable

interface EntityRepositoryUni {

    fun <T : Entity> persist(sessionObject: SessionObject,entity: T): Uni<T>

    fun <T : Entity> merge(sessionObject: SessionObject,entity: T): Uni<T>

    fun <T : Entity> save(sessionObject: SessionObject,entity: T): Uni<T>

    fun <T : Entity> exists(sessionObject: SessionObject,clazz: Class<T>, id: Serializable): Uni<Boolean>

    fun <T : Entity> remove(sessionObject: SessionObject,entity:T):Uni<Unit>

    fun <T : Entity> get(sessionObject: SessionObject,clazz: Class<T>, id: Serializable): Uni<T?>

    fun <T : Entity> batchSave(sessionObject: SessionObject,entityList:Array<T>): Uni<Boolean>

    fun <T : Entity> delete(sessionObject: SessionObject,clazz: Class<T>?, id: Serializable?): Uni<Boolean>

    fun <T> singleQuery(sessionObject: SessionObject,clazz: Class<T>?,sql:String,params:Map<String,Any> = HashMap()):Uni<T?>

    fun <T> listQuery(sessionObject: SessionObject,clazz: Class<T>?,sql:String,params:Map<String,Any> = HashMap()):Uni<List<T>>

    fun executeUpdate(sessionObject: SessionObject,sql:String,params:Map<String,Any> = HashMap()):Uni<Int>

    fun <T> withTransaction(work: java.util.function.Function<SessionObject, Uni<T>>): Future<T>

}