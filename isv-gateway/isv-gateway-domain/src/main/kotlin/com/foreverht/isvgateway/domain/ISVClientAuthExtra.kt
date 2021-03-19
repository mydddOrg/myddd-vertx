package com.foreverht.isvgateway.domain

abstract class ISVClientAuthExtra {

    lateinit var clientType:ISVClientType

    abstract fun clientTokenValid():Boolean
}