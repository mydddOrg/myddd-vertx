package org.myddd.vertx.oauth2.api

data class OAuth2TokenDTO(val accessToken:String,val refreshToken:String,val accessExpiredIn:Long,val refreshExpiredIn:Long)
