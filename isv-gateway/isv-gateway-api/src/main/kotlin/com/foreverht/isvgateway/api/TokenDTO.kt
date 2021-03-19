package com.foreverht.isvgateway.api

data class TokenDTO(val accessToken:String, var refreshToken:String, val accessExpiredIn:Long, val refreshExpiredIn:Long? = 0)