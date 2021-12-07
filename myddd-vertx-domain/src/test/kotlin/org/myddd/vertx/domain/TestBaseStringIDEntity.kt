package org.myddd.vertx.domain

import io.vertx.junit5.VertxExtension
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.myddd.vertx.domain.mock.MockBaseStringIDEntity

@ExtendWith(VertxExtension::class,IOCInitExtension::class)
class TestBaseStringIDEntity {

    @Test
    fun testCreatePerson(){
        val person = MockBaseStringIDEntity()
        Assertions.assertThat(person.id).isNotNull
    }
}