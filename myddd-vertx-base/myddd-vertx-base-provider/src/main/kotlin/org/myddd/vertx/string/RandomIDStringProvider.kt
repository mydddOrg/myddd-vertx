package org.myddd.vertx.string

import org.apache.commons.lang3.RandomStringUtils

class RandomIDStringProvider : RandomIDString {

    override fun randomString(count: Int): String {
        return RandomStringUtils.random(count,true,true)
    }
}