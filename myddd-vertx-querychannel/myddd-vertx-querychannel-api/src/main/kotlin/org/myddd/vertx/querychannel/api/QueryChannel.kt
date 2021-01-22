package org.myddd.vertx.querychannel.api

import io.vertx.core.Future

interface QueryChannel {

    /**
     * 执行一个分页查询,返回分页查询
     */
    fun <T> pageQuery(pageQuery: PageQuery<T>):Future<Page<T>>

    /**
     * 执行一个非分页查询
     */
    fun <T> queryList(clazz: Class<T>,sql:String,params:List<Any> = ArrayList()):Future<List<T>>


}