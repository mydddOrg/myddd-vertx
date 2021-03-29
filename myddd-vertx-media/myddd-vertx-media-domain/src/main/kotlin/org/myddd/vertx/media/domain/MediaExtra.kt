package org.myddd.vertx.media.domain

abstract class MediaExtra(val mediaType:MediaType){
    abstract fun destPath():String
}