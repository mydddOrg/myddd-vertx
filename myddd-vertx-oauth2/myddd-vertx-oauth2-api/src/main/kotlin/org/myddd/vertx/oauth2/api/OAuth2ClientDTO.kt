package org.myddd.vertx.oauth2.api

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class OAuth2ClientDTO constructor(
    val id:Long? ,
    val version:Long?,
    val clientId:String,
    val clientSecret:String?,
    val name:String,
    val description:String?,
    val disabled:Boolean = false) {

    @JsonCreator
    constructor(@JsonProperty("clientId") clientId: String, @JsonProperty("name") name: String, @JsonProperty("description") description: String?) : this(clientId = clientId,name = name,description = description,id = null,version = null,clientSecret = null)

    fun validForCreate():Boolean {
        return id == null && version == null && clientSecret == null
    }


}