package org.myddd.vertx.id

import com.github.guepardoapps.kulid.ULID

class ULIDStringGenerator:StringIDGenerator {
    override fun nextId(): String = ULID.generate(System.currentTimeMillis(), byteArrayOf(0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9))
}