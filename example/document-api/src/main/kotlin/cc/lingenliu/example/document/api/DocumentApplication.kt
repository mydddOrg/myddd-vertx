package cc.lingenliu.example.document.api

import io.vertx.core.Future

interface DocumentApplication {

    suspend fun createDocument(documentDTO:DocumentDTO):Future<DocumentDTO>

    suspend fun queryDocumentById(id:Long):Future<DocumentDTO?>
}