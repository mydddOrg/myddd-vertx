package com.foreverht.isvgateway.domain

import io.vertx.core.Future
import org.myddd.vertx.domain.BaseEntity
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.string.RandomIDString
import javax.persistence.*

@Entity
@Table(name="proxy_media",
    indexes = [
        Index(name = "index_media_id",columnList = "media_id"),
        Index(name = "index_digest",columnList = "digest")
    ],
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["media_id"]),
        UniqueConstraint(columnNames = ["digest"])
    ])
class ProxyMedia: BaseEntity() {

    @Column(name = "media_id",nullable = false,length = 32)
    lateinit var mediaId:String

    lateinit var digest:String

    lateinit var name:String

    var size:Long = 0

    @Column(name="relate_id",nullable = false,length = 128)
    lateinit var relateId:String


    companion object {

        private val repository by lazy { InstanceFactory.getInstance(ProxyMediaRepository::class.java) }

        private val randomIDString by lazy { InstanceFactory.getInstance(RandomIDString::class.java) }

        suspend fun queryMediaById(mediaId:String):Future<ProxyMedia?>{
            return try {
                repository.singleQuery(
                    clazz = ProxyMedia::class.java,
                    sql = "from ProxyMedia where mediaId = :mediaId",
                    params = mapOf(
                        "mediaId" to mediaId
                    )
                )
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }

        suspend fun queryMediaByDigest(digest:String):Future<ProxyMedia?>{
            return try {
                repository.singleQuery(
                    clazz = ProxyMedia::class.java,
                    sql = "from ProxyMedia where digest = :digest",
                    params = mapOf(
                        "digest" to digest
                    )
                )
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }

    }


    suspend fun createProxyMedia():Future<ProxyMedia>{
        return try {
            this.mediaId = randomIDString.randomUUID()
            repository.save(this)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }


}