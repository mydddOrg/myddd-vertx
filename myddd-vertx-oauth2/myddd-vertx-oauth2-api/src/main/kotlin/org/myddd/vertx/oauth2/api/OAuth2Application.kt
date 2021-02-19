package org.myddd.vertx.oauth2.api

import io.vertx.core.Future

interface OAuth2Application {

    /**
     * 根据client与client secret,返回当前请求TOKEN的用户
     */
    suspend fun requestClientToken(clientId:String, clientSecret:String):Future<OAuth2UserDTO?>

    /**
     * 刷新User的Token
     */
    suspend fun refreshUserToken(clientId: String, refreshToken: String):Future<OAuth2UserDTO?>

    /**
     * 取消User的Token
     */
    suspend fun revokeUserToken(clientId: String, accessToken:String):Future<Boolean>

    /**
     * 加载当前用户最新的TOKEN信息
     */
    suspend fun loadUserToken(clientId: String):Future<OAuth2UserDTO?>

}