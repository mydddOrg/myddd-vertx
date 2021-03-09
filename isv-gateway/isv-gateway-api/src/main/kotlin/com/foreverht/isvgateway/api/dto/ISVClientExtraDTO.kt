package com.foreverht.isvgateway.api.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.foreverht.isvgateway.api.dto.extra.ISVClientExtraForWorkPlusDTO


@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "clientType")
@JsonSubTypes(value = [JsonSubTypes.Type(value = ISVClientExtraForWorkPlusDTO::class, name = "WorkPlus_App")])
abstract class ISVClientExtraDTO(var clientType:String)
