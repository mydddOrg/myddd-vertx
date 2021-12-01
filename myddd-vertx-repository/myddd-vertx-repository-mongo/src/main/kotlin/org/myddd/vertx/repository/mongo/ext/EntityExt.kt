package org.myddd.vertx.repository.mongo.ext

import org.myddd.vertx.domain.Entity
import javax.persistence.Table

fun <T: Entity> Class<T>.collectionName(): String {
    val tableAnnotation = this.annotations.asList().stream().filter{it is Table}.findFirst()
    if(tableAnnotation.isPresent){
        val name = (tableAnnotation.get() as Table).name
        return name.ifEmpty { this.simpleName }
    }
    return this.simpleName
}

fun Entity.collectionName():String {
    return this.javaClass.collectionName()
}


