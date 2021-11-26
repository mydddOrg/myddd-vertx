package org.myddd.vertx.id

import com.github.guepardoapps.kulid.ULID

class ULIDStringGenerator:StringIDGenerator {
    override fun nextId(): String = ULID.random()
}