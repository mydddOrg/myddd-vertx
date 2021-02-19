package org.myddd.vertx.oauth2.api

import io.vertx.core.Future

interface OAuth2ClientApplication {

    /**
     * 创建一个OAUTH2 Client
     */
    suspend fun createClient(clientDTO: OAuth2ClientDTO):Future<OAuth2ClientDTO>

    /**
     * 根据CLIENT ID查询一个Client
     */
    suspend fun queryClient(clientId: String):Future<OAuth2ClientDTO?>
    /**
     * 重置OAUTH2 Client的secret
     */
    suspend fun resetClientSecret(clientId:String):Future<String>

    /**
     * 标记一个OAUTH2 Client为可用状态
     */
    suspend fun enableClient(clientId:String):Future<Boolean>

    /**
     * 标记一个OAUTH2 Client状态为不可用
     */
    suspend fun disableClient(clientId: String):Future<Boolean>
}