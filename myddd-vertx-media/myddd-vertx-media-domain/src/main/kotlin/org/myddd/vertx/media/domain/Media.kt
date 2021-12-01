package org.myddd.vertx.media.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSetter
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.file.FileDigest
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.media.MediaNotFoundException
import org.myddd.vertx.media.SourceFileNotExistsException
import org.myddd.vertx.media.domain.converter.MediaExtraConverter
import java.io.File
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name="media_",
    indexes = [
        Index(name = "index_digest",columnList = "digest")
    ],
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["digest"])
    ])
@JsonInclude(JsonInclude.Include.NON_NULL)
class Media: org.myddd.vertx.domain.Entity {

    @Id
    @JsonProperty(value = "_id")
    var id:String? = null

    @Version
    var version:Long = 0

    override var created:Long = System.currentTimeMillis()

    override var updated:Long = 0

    lateinit var digest:String

    lateinit var name:String

    var size:Long = 0

    @Column(name = "extra",length = 256)
    @Convert(converter = MediaExtraConverter::class)
    lateinit var extra: MediaExtra

    override fun setId(id: Serializable) {
        this.id = id as String
    }

    @JsonIgnore
    override fun getId(): Serializable {
        return this.id!!
    }

    companion object {

        private val vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }
        private val repository by lazy { InstanceFactory.getInstance(MediaRepository::class.java) }
        private val fileDigest by lazy { InstanceFactory.getInstance(FileDigest::class.java) }
        private val mediaStorage by lazy { InstanceFactory.getInstance(MediaStorage::class.java) }

        suspend fun downloadByMediaId(mediaId: String):Future<String>{
            val exist = queryMediaById(mediaId).await()
            if(Objects.isNull(exist)){
                throw MediaNotFoundException(arrayOf(mediaId))
            }

            return mediaStorage.downloadFromStorage(exist!!.extra)
        }


        suspend fun queryMediaById(mediaId:String):Future<Media?>{
            return repository.queryByMediaId(mediaId)
        }

        suspend fun queryMediaByDigest(digest:String):Future<Media?>{
            return repository.queryByDigest(digest)
        }

        suspend fun createByLocalFile(path:String):Future<Media>{
            val media = mediaFromFile(path).await()
            val exists = queryMediaByDigest(digest = media.digest).await()
            return if(Objects.nonNull(exists)){
                Future.succeededFuture(exists)
            }else{
                val file = MediaFile.of(path).await()
                val extra = mediaStorage.uploadToStorage(file).await()
                media.extra = extra
                media.id = repository.nextId()
                repository.saveMedia(media)
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