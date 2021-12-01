package org.myddd.vertx.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
abstract class DocumentEntity:Entity{

    @JsonProperty(value = "_id")
    var id:String? = null

    final override var created:Long = System.currentTimeMillis()

    override var updated:Long = 0

    @JsonIgnore
    override fun getId(): Serializable {
        return id!!
    }

}