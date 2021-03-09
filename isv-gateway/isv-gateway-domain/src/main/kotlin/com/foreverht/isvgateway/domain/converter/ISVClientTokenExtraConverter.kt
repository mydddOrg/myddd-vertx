package com.foreverht.isvgateway.domain.converter

import com.foreverht.isvgateway.domain.ISVClientTokenExtra
import com.foreverht.isvgateway.domain.ISVClientType
import com.foreverht.isvgateway.domain.ISVErrorCode
import com.foreverht.isvgateway.domain.extra.ISVClientTokenExtraForWorkPlusApp
import io.vertx.core.json.JsonObject
import org.myddd.vertx.base.BusinessLogicException
import javax.persistence.AttributeConverter

class ISVClientTokenExtraConverter: AttributeConverter<ISVClientTokenExtra, String> {
    override fun convertToDatabaseColumn(attribute: ISVClientTokenExtra?): String {
        return JsonObject.mapFrom(attribute).toString()
    }

    override fun convertToEntityAttribute(dbData: String?): ISVClientTokenExtra {
        val jsonObject = JsonObject(dbData)

        return when (jsonObject.getString("clientType").toUpperCase()){
            ISVClientType.WorkPlusApp.toString().toUpperCase() -> jsonObject.mapTo(ISVClientTokenExtraForWorkPlusApp::class.java)
            else -> throw BusinessLogicException(ISVErrorCode.CLIENT_TYPE_NOT_SUPPORT)
        }
    }
}