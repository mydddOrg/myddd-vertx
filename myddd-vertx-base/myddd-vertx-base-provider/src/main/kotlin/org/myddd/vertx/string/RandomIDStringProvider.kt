package org.myddd.vertx.string

import org.apache.commons.lang3.RandomStringUtils
import java.util.*

class RandomIDStringProvider : RandomIDString {

    override fun randomString(count: Int): String {
        return RandomStringUtils.random(count,true,true)
    }

    override fun randomUUID(): String {
        return UUID.randomUUID().toString().replace("-","")
    }
}