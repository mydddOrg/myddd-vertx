package com.foreverht.isvgateway.domain

import com.foreverht.isvgateway.domain.converter.ISVClientTokenExtraConverter
import io.vertx.core.Future
import org.myddd.vertx.domain.BaseEntity
import org.myddd.vertx.ioc.InstanceFactory
import javax.persistence.*

@Entity
@Table(name = "isv_client_token",
    indexes = [
        Index(name = "index_token_client_id",columnList = "client_id")
    ],
    uniqueConstraints = [UniqueConstraint(columnNames = ["client_id","client_type"])])
class ISVClientToken : BaseEntity() {

    @Column(name = "client_id")
    lateinit var clientId:String

    @Column(name = "client_type")
    lateinit var clientType:ISVClientType

    @Column(name = "client_token")
    lateinit var token:String

    @Column(name = "extra",length = 500)
    @Convert(converter = ISVClientTokenExtraConverter::class)
    lateinit var extra: ISVClientTokenExtra

    companion object {
        private val isvClientRepository by lazy { InstanceFactory.getInstance(ISVClientRepository::class.java) }
    }

    suspend fun saveClientToken():Future<ISVClientToken>{
       return isvClientRepository.save(this)
    }


}