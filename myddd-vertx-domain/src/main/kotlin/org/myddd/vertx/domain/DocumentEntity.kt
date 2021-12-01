package org.myddd.vertx.domain

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
abstract class DocumentEntity{

    @JsonProperty(value = "_id")
    var id:String? = null

    var created:Long = System.currentTimeMillis()

    var updated:Long = 0

}