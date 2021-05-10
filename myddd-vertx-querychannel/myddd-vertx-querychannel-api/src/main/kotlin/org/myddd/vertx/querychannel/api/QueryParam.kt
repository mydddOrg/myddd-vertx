package org.myddd.vertx.querychannel.api

data class QueryParam<T>(
    val clazz: Class<T>,
    val sql:String,
    val params:Map<String,Any> = emptyMap()
) {
    fun countSQL():String {
        return when{
            sql.startsWith("select",true)  -> "select count(*) from ($sql)"
            else ->"select count(*) $sql"
        }
    }
}