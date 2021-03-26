package com.foreverht.isvgateway.domain

import io.vertx.core.Future
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.domain.BaseEntity
import org.myddd.vertx.ioc.InstanceFactory
import javax.persistence.*

@Entity
@Table(name = "proxy_employee",
    indexes = [
        Index(name = "index_auth_code_id",columnList = "auth_code_id"),
        Index(name = "index_user_id",columnList = "user_id")
    ],
    uniqueConstraints = [UniqueConstraint(columnNames = ["auth_code_id","user_id"])]
)
class ProxyEmployee: BaseEntity() {

    @ManyToOne(cascade = [],fetch = FetchType.EAGER)
    @JoinColumn(name = "auth_code_id")
    lateinit var authCode:ISVAuthCode

    @Column(name = "user_id",nullable = false)
    lateinit var userId:String

    lateinit var name:String

    var avatar:String? = null

    var mobile:String? = null

    var email:String? = null

    @OneToMany(cascade=[CascadeType.ALL],fetch=FetchType.LAZY,mappedBy = "employee")
    var relations:List<ProxyEmpOrgRelation> = arrayListOf()

    companion object {
        private val proxyRepository by lazy { InstanceFactory.getInstance(ProxyRepository::class.java) }

        suspend fun queryByAuthCode(authCodeId:Long):Future<List<ProxyEmployee>>{
            return try {
                return proxyRepository.listQuery(ProxyEmployee::class.java,"from ProxyEmployee where authCode.id = :authCodeId",
                    mapOf("authCodeId" to authCodeId))
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }

        suspend fun batchSaveEmployeeList(isvAuthCodeId:Long,employeeList:List<ProxyEmployee>):Future<Unit>{
            return try {
                proxyRepository.syncEmployeeList(isvAuthCodeId = isvAuthCodeId,employeeList = employeeList).await()
                Future.succeededFuture()
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }

        suspend fun queryEmployee(authCodeId: Long,userId:String):Future<ProxyEmployee?>{
            return try {
                 proxyRepository.singleQuery(
                    clazz = ProxyEmployee::class.java,
                    sql = "from ProxyEmployee where authCode.id = :authCodeId and userId = :userId",
                    mapOf(
                        "authCodeId" to authCodeId,
                        "userId" to userId
                    )
                )
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }
    }

    suspend fun createEmployee():Future<ProxyEmployee>{
        return try {
            return proxyRepository.save(this)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

}