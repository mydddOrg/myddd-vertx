package org.myddd.vertx.domain

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.domain.mock.MockBaseStringIDEntity

class TestBaseStringIDEntity:AbstractDomainTest() {

    @Test
    fun testCreatePerson(){
        val person = MockBaseStringIDEntity()
        Assertions.assertThat(person.id).isNotNull
    }
}