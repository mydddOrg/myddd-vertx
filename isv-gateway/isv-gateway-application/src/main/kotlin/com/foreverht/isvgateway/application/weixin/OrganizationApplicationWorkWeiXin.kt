package com.foreverht.isvgateway.application.weixin

import com.foreverht.isvgateway.api.OrganizationApplication
import com.foreverht.isvgateway.api.dto.EmployeeDTO
import com.foreverht.isvgateway.api.dto.OrgPageQueryDTO
import com.foreverht.isvgateway.api.dto.OrganizationDTO
import com.foreverht.isvgateway.application.AbstractApplication
import com.foreverht.isvgateway.application.assembler.toEmployeeDTO
import com.foreverht.isvgateway.application.assembler.toOrganizationDTO
import com.foreverht.isvgateway.domain.ISVAuthCode
import com.foreverht.isvgateway.domain.ISVClientType
import com.foreverht.isvgateway.domain.ISVErrorCode
import com.foreverht.isvgateway.domain.ProxyOrganization
import com.foreverht.isvgateway.domain.extra.ISVClientExtraForWorkWeiXin
import io.vertx.core.Future
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.querychannel.api.PageParam
import org.myddd.vertx.querychannel.api.QueryChannel
import org.myddd.vertx.querychannel.api.QueryParam
import java.util.*
import kotlin.streams.toList

class OrganizationApplicationWorkWeiXin: AbstractApplication(),OrganizationApplication {


    companion object{
        private const val WEI_XIN_ROOT_ORG_ID = "1"
    }

    private val queryChannel by lazy { InstanceFactory.getInstance(QueryChannel::class.java) }

    override suspend fun queryOrganizationById(isvAccessToken: String, orgCode: String, orgId: String?): Future<OrganizationDTO> {
        return try {
            val organization = getOrganization(isvAccessToken,orgCode,orgId).await()
            Future.succeededFuture(toOrganizationDTO(organization))

        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun queryChildrenOrganizations(orgPageQueryDTO: OrgPageQueryDTO): Future<List<OrganizationDTO>> {
        return try {
            val queryOrgId = if(Objects.isNull(orgPageQueryDTO.orgId)) WEI_XIN_ROOT_ORG_ID else orgPageQueryDTO.orgId!!

            val (isvAuthCode,_) = getAuthCode(isvAccessToken = orgPageQueryDTO.accessToken).await()

            val pageQueryDTO = queryChannel.pageQuery(
                queryParam = QueryParam(
                    clazz = ProxyOrganization::class.java,
                    sql = "from ProxyOrganization where authCode.id = :authCodeId and orgCode = :orgCode and orgId = :orgId",
                    params = mapOf(
                        "authCodeId" to isvAuthCode!!.id,
                        "orgCode" to orgPageQueryDTO.orgCode,
                        "orgId" to queryOrgId
                    )
                ),
                pageParam = PageParam(
                    skip = orgPageQueryDTO.skip,
                    limit = orgPageQueryDTO.limit
                )
            ).await()

            val organizationList = pageQueryDTO.dataList.stream().map { toOrganizationDTO(it) }.toList()
            Future.succeededFuture(organizationList)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun queryOrganizationEmployees(orgPageQueryDTO: OrgPageQueryDTO): Future<List<EmployeeDTO>> {
        return try {

            val organization = getOrganization(
                isvAccessToken = orgPageQueryDTO.accessToken,
                orgCode = orgPageQueryDTO.orgCode,
                orgId = orgPageQueryDTO.orgId).await()

            val employeeList = organization.queryEmployee().await()

            Future.succeededFuture(employeeList.stream().map { toEmployeeDTO(it) }.toList())
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    private suspend fun getOrganization(isvAccessToken: String, orgCode: String, orgId: String?):Future<ProxyOrganization>{
        return try {
            val queryOrgId = if(Objects.isNull(orgId)) WEI_XIN_ROOT_ORG_ID else orgId!!
            val (isvAuthCode,_)  = getAuthCode(isvAccessToken = isvAccessToken).await()

            val organization = ProxyOrganization.queryOrganization(authCodeId = isvAuthCode.id,orgCode = isvAuthCode.orgCode,orgId = queryOrgId).await()
            requireNotNull(organization){
                "未找到对应的organization"
            }


            Future.succeededFuture(organization)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }

    }
}