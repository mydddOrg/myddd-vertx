package com.foreverht.isvgateway.domain

abstract class ISVClientTokenExtra {

    lateinit var clientType:ISVClientType

    abstract fun accessTokenValid():Boolean

    abstract fun accessToken():String

}