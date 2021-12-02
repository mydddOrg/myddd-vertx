package org.myddd.vertx.domain.ext

import org.myddd.vertx.domain.Document
import javax.persistence.Index
import javax.persistence.Table
import javax.persistence.UniqueConstraint

fun <T: Document> Class<T>.collectionName(): String {
    val tableAnnotation = this.annotations.asList().stream().filter{it is Table}.findFirst()
    if(tableAnnotation.isPresent){
        val name = (tableAnnotation.get() as Table).name
        return name.ifEmpty { this.simpleName }
    }
    return this.simpleName
}

fun <T: Document> Class<T>.indexes(): Array<Index> {
    val tableAnnotation = this.annotations.asList().stream().filter{it is Table}.findFirst()
    if(tableAnnotation.isPresent){
        return (tableAnnotation.get() as Table).indexes
    }
    return emptyArray()
}

fun <T: Document> Class<T>.uniqueConstraints(): Array<UniqueConstraint> {
    val tableAnnotation = this.annotations.asList().stream().filter{it is Table}.findFirst()
    if(tableAnnotation.isPresent){
        return (tableAnnotation.get() as Table).uniqueConstraints
    }
    return emptyArray()
}

fun Document.collectionName():String {
    return this.javaClass.collectionName()
}





