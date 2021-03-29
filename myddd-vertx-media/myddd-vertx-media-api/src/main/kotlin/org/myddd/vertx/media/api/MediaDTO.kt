package org.myddd.vertx.media.api

data class MediaDTO(val mediaId:String,val digest:String,val name:String,val size:Long,val extra:Map<String,Any>)