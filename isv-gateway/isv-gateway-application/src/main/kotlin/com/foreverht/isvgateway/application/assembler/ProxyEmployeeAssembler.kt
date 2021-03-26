package com.foreverht.isvgateway.application.assembler

import com.foreverht.isvgateway.api.dto.EmployeeDTO
import com.foreverht.isvgateway.domain.ProxyEmployee

fun toEmployeeDTO(proxyEmployee: ProxyEmployee):EmployeeDTO {
    return EmployeeDTO(
        userId = proxyEmployee.userId,
        name = proxyEmployee.name,
        avatar = proxyEmployee.avatar,
        mobile = proxyEmployee.mobile
    )
}