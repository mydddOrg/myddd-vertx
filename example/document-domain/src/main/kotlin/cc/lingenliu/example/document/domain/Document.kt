package cc.lingenliu.example.document.domain

import io.vertx.core.Future
import org.myddd.vertx.domain.BaseEntity
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.string.RandomIDString
import javax.persistence.*

@Entity
@Table(name = "document",
    indexes = [
        Index(name = "index_media_id",columnList = "media_id"),
    ],
    uniqueConstraints = [UniqueConstraint(columnNames = ["media_id"])]
)
class Document: BaseEntity() {

    @Column(nullable = false,length = 36)
    lateinit var name:String

    @Column(name = "media_id",nullable = false,length = 36)
    lateinit var mediaId:String

    lateinit var md5:String

    lateinit var suffix: String

    @Column(name = "document_type")
    lateinit var documentType:DocumentType

    companion object {
        private val repository by lazy { InstanceFactory.getInstance(DocumentRepository::class.java) }

        suspend fun queryDocumentById(id:Long):Future<Document?>{
            return repository.get(Document::class.java,id)
        }
    }

    suspend fun createDocument():Future<Document>{
        return try {
            repository.save(this)
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

}