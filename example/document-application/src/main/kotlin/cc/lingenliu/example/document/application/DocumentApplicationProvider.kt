package cc.lingenliu.example.document.application

import cc.lingenliu.example.document.api.DocumentApplication
import cc.lingenliu.example.document.api.DocumentDTO
import cc.lingenliu.example.document.application.assembler.toDocument
import cc.lingenliu.example.document.application.assembler.toDocumentDTO
import cc.lingenliu.example.document.domain.Document
import io.vertx.core.Future
import io.vertx.kotlin.coroutines.await
import java.util.*

class DocumentApplicationProvider:DocumentApplication {

    override suspend fun createDocument(documentDTO: DocumentDTO): Future<DocumentDTO> {
        return try {
            val created = toDocument(documentDTO).createDocument().await()
            Future.succeededFuture(toDocumentDTO(created))
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    override suspend fun queryDocumentById(id: Long): Future<DocumentDTO?> {
        return try {
            val queryDocument = Document.queryDocumentById(id).await()
            if(Objects.nonNull(queryDocument)){
                Future.succeededFuture(toDocumentDTO(queryDocument!!))
            }else{
                Future.succeededFuture(null)
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}