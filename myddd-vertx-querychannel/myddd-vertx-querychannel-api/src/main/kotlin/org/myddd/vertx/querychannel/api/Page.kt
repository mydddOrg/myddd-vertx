package org.myddd.vertx.querychannel.api

data class Page<T>(val dataList:List<T>,val totalCount:Long,val page:Int,var pageSize:Int)
