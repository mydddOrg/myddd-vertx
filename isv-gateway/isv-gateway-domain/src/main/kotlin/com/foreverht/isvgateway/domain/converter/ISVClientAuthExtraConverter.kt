package com.foreverht.isvgateway.domain.converter

import com.foreverht.isvgateway.domain.ISVClientAuthExtra
import com.foreverht.isvgateway.domain.ISVClientType
import com.foreverht.isvgateway.domain.ISVErrorCode
import com.foreverht.isvgateway.domain.extra.ISVClientAuthExtraForISV
import io.vertx.core.json.JsonObject
import org.myddd.vertx.base.BusinessLogicException
import java.util.*
import javax.persistence.AttributeConverter

class ISVClientAuthExtraConverter: AttributeConverter<ISVClientAuthExtra?,String?> {

    override fun convertToDatabaseColumn(attribute: ISVClientAuthExtra?): String? {
        return if(Objects.isNull(attribute)) null else JsonObject.mapFrom(attribute).toString()
    }

    override fun convertToEntityAttribute(dbData: String?): ISVClientAuthExtra? {
         if(Objects.isNull(dbData)) return null

        val jsonObject = JsonObject(dbData)
        return when (jsonObject.getString("clientType").toUpperCase()){
            ISVClientType.WorkPlusISV.toString().toUpperCase() -> jsonObject.mapTo(ISVClientAuthExtraForISV::class.java)
            else -> throw BusinessLogicException(ISVErrorCode.CLIENT_TYPE_NOT_SUPPORT)
        }
    }
}