package org.myddd.vertx.querychannel.api

data class PageQuery<T>(var clazz: Class<T>, var sql:String, var params:Map<String,Any> = emptyMap(), var page:Int = 0, var pageSize:Int = 20) {
    fun countSQL():String {
        when{
            sql.startsWith("select",true)  -> "select count(*) ($sql)"
        }
        return "select count(*) $sql"
    }
}
