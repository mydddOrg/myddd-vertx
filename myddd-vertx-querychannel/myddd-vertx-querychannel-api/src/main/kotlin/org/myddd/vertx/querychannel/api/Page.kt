package org.myddd.vertx.querychannel.api

data class Page<T>(val dataList:List<T>,val totalCount:Long,val skip:Int,var limit:Int)
