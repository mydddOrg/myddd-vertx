package org.myddd.vertx.querychannel.api

data class QueryParam<T>(var clazz: Class<T>, var sql:String, var params:Map<String,Any> = emptyMap()) {
    fun countSQL():String {
        when{
            sql.startsWith("select",true)  -> "select count(*) ($sql)"
        }
        return "select count(*) $sql"
    }
}
