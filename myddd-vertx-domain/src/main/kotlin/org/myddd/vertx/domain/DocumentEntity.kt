package org.myddd.vertx.domain

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
abstract class DocumentEntity:Document{

    var created:Long = System.currentTimeMillis()

    var updated:Long = 0

    @JsonProperty(value = "_id")
    override var id:String? = null
}