package org.myddd.vertx.i18n

import io.vertx.core.Future

interface I18N {
    /**
     * 传入KEY以及参数，获取一个国际化的错误描述
     */
    suspend fun getMessage(key:String, params:Array<String> = emptyArray(), language:String? = null): Future<String>

}