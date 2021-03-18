package com.foreverht.isvgateway.domain.converter

import com.foreverht.isvgateway.domain.ISVAuthExtra
import com.foreverht.isvgateway.domain.ISVClientType
import com.foreverht.isvgateway.domain.ISVErrorCode
import com.foreverht.isvgateway.domain.extra.ISVAuthExtraForISV
import io.vertx.core.json.JsonObject
import org.myddd.vertx.base.BusinessLogicException
import java.util.*
import javax.persistence.AttributeConverter

class ISVAuthExtraConverter: AttributeConverter<ISVAuthExtra?,String?> {

    override fun convertToDatabaseColumn(attribute: ISVAuthExtra?): String? {
        return if(Objects.isNull(attribute)) null else JsonObject.mapFrom(attribute).toString()
    }

    override fun convertToEntityAttribute(dbData: String?): ISVAuthExtra? {
         if(Objects.isNull(dbData)) return null

        val jsonObject = JsonObject(dbData)
        return when (jsonObject.getString("clientType").toUpperCase()){
            ISVClientType.WorkPlusISV.toString().toUpperCase() -> jsonObject.mapTo(ISVAuthExtraForISV::class.java)
            else -> throw BusinessLogicException(ISVErrorCode.CLIENT_TYPE_NOT_SUPPORT)
        }
    }
}