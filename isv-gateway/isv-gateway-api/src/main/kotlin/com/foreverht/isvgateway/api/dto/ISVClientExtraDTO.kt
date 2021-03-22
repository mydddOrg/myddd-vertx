package com.foreverht.isvgateway.api.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.foreverht.isvgateway.api.dto.extra.ISVClientExtraForWorkPlusDTO
import com.foreverht.isvgateway.api.dto.extra.ISVClientExtraForWorkPlusISVDTO
import com.foreverht.isvgateway.api.dto.extra.ISVClientExtraForWorkWeiXinDTO


@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "clientType")
@JsonSubTypes(value = [
    JsonSubTypes.Type(value = ISVClientExtraForWorkPlusDTO::class, name = "WorkPlusApp"),
    JsonSubTypes.Type(value = ISVClientExtraForWorkPlusISVDTO::class, name = "WorkPlusISV"),
    JsonSubTypes.Type(value = ISVClientExtraForWorkWeiXinDTO::class, name = "WorkWeiXin"),
])
abstract class ISVClientExtraDTO(var clientType:String)
