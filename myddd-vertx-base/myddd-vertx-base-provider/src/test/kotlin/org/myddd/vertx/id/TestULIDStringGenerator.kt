package org.myddd.vertx.id

import com.github.guepardoapps.kulid.ULID
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class TestULIDStringGenerator {

    private val stringIDGenerator by lazy { ULIDStringGenerator() }

    @Test
    fun testULIDGenerator(){
        val randomUlid = ULID.random()
        Assertions.assertThat(randomUlid).isNotEmpty

        val generateUlid = ULID.generate(System.currentTimeMillis(), byteArrayOf(0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9))
        Assertions.assertThat(generateUlid).isNotEmpty
    }

    @Test
    fun testNextId(){
        Assertions.assertThat(stringIDGenerator.nextId()).isNotNull
    }
}