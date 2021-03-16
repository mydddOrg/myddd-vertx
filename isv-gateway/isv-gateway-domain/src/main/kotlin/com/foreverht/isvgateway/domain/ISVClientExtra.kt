package com.foreverht.isvgateway.domain

abstract class ISVClientExtra {

    lateinit var clientType:ISVClientType

    abstract fun primaryId():String

}