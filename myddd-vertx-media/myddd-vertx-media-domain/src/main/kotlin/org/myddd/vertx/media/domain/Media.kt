package org.myddd.vertx.media.domain

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.base.BusinessLogicException
import org.myddd.vertx.domain.BaseEntity
import org.myddd.vertx.file.FileDigest
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.media.MediaErrorCode
import org.myddd.vertx.media.SourceFileNotExistsException
import org.myddd.vertx.media.domain.converter.MediaExtraConverter
import org.myddd.vertx.string.RandomIDString
import java.io.File
import java.util.*
import javax.persistence.*

@Entity
@Table(name="media",
    indexes = [
        Index(name = "index_media_id",columnList = "media_id"),
        Index(name = "index_digest",columnList = "digest")
    ],
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["media_id"]),
        UniqueConstraint(columnNames = ["digest"])
    ])
class Media: BaseEntity() {

    @Column(name = "media_id",nullable = false,length = 32)
    lateinit var mediaId:String

    lateinit var digest:String

    lateinit var name:String

    var size:Long = 0

    @Column(name = "extra",length = 256)
    @Convert(converter = MediaExtraConverter::class)
    lateinit var extra: MediaExtra


    companion object {

        private val repository by lazy { InstanceFactory.getInstance(MediaRepository::class.java) }

        private val randomIDString by lazy { InstanceFactory.getInstance(RandomIDString::class.java) }
        private val fileDigest by lazy { InstanceFactory.getInstance(FileDigest::class.java) }

        private val mediaStorage by lazy { InstanceFactory.getInstance(MediaStorage::class.java) }

        private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }

        suspend fun queryMediaById(mediaId:String):Future<Media?>{
            return try {
                repository.singleQuery(
                    clazz = Media::class.java,
                    sql = "from Media where mediaId = :mediaId",
                    params = mapOf(
                        "mediaId" to mediaId
                    )
                )
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }

        suspend fun queryMediaByDigest(digest:String):Future<Media?>{
            return try {
                repository.singleQuery(
                    clazz = Media::class.java,
                    sql = "from Media where digest = :digest",
                    params = mapOf(
                        "digest" to digest
                    )
                )
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }

        suspend fun createByLocalFile(path:String):Future<Media>{
            return try {
                val media = mediaFromFile(path).await()
                val exists = queryMediaByDigest(digest = media.digest).await()
                return if(Objects.nonNull(exists)){
                    Future.succeededFuture(exists)
                }else{
                    val file = MediaFile.of(path).await()
                    val extra = mediaStorage.uploadToStorage(file).await()

                    media.mediaId = randomIDString.randomUUID()
                    media.extra = extra
                    repository.save(media)
                }

            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }

        private suspend fun mediaFromFile(path: String):Future<Media>{
            return try{
                val fs = vertx.fileSystem()
                val exists = fs.exists(path).await()
                if(!exists){
                    throw SourceFileNotExistsException()
                }
                val fileSystemProps = fs.lprops(path).await()
                val media = Media()
                media.name = path.substring(path.lastIndexOf(File.separator) + 1)
                media.digest = fileDigest.digest(path).await()
                media.size = fileSystemProps.size()

                Future.succeededFuture(media)
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }
    }
}