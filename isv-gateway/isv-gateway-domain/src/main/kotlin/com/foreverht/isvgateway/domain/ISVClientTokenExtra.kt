package com.foreverht.isvgateway.domain

import javax.persistence.Transient

abstract class ISVClientTokenExtra {

    lateinit var clientType:ISVClientType

    abstract fun accessTokenValid():Boolean
}