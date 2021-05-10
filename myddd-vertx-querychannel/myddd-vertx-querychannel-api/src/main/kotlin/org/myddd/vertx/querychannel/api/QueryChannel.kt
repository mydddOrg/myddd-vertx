package org.myddd.vertx.querychannel.api

import io.vertx.core.Future

interface QueryChannel {

    /**
     * 执行一个分页查询,返回分页查询
     */
    suspend fun <T> pageQuery(queryParam: QueryParam<T>,pageParam: PageParam = PageParam()):Future<Page<T>>

    /**
     * 执行一个非分页查询
     */
    suspend fun <T> queryList(queryParam: QueryParam<T>):Future<List<T>>


    suspend fun <T> limitQueryList(queryParam: QueryParam<T>,limit:Int = 50):Future<List<T>>

}