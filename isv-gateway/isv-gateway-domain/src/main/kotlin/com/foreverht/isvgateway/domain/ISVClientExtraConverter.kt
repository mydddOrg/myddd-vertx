package com.foreverht.isvgateway.domain

import com.foreverht.isvgateway.domain.extra.ISVClientExtraForWorkPlusApp
import io.vertx.core.json.JsonObject
import org.myddd.vertx.base.BusinessLogicException
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class ISVClientExtraConverter : AttributeConverter<ISVClientExtra,String> {

    override fun convertToDatabaseColumn(attribute: ISVClientExtra?): String {
        return JsonObject.mapFrom(attribute).toString()
    }

    override fun convertToEntityAttribute(dbData: String?): ISVClientExtra {
        val jsonObject = JsonObject(dbData)

        return when (jsonObject.getString("clientType").toUpperCase()){

            ISVClientType.WorkPlus.toString().toUpperCase() -> jsonObject.mapTo(ISVClientExtraForWorkPlusApp::class.java)

            else -> throw BusinessLogicException(ISVErrorCode.CLIENT_TYPE_NOT_SUPPORT)
        }
    }
}