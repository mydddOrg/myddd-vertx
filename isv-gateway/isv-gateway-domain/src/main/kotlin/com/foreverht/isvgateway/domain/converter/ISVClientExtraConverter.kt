package com.foreverht.isvgateway.domain.converter

import com.foreverht.isvgateway.domain.ISVClientExtra
import com.foreverht.isvgateway.domain.ISVClientType
import com.foreverht.isvgateway.domain.ISVErrorCode
import com.foreverht.isvgateway.domain.extra.ISVClientExtraForWorkPlusApp
import com.foreverht.isvgateway.domain.extra.ISVClientExtraForWorkPlusISV
import com.foreverht.isvgateway.domain.extra.ISVClientExtraForWorkWeiXin
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
            ISVClientType.WorkPlusApp.toString().toUpperCase() -> jsonObject.mapTo(ISVClientExtraForWorkPlusApp::class.java)
            ISVClientType.WorkPlusISV.toString().toUpperCase() -> jsonObject.mapTo(ISVClientExtraForWorkPlusISV::class.java)
            ISVClientType.WorkWeiXin.toString().toUpperCase() -> jsonObject.mapTo(ISVClientExtraForWorkWeiXin::class.java)
            else -> throw BusinessLogicException(ISVErrorCode.CLIENT_TYPE_NOT_SUPPORT)
        }
    }
}