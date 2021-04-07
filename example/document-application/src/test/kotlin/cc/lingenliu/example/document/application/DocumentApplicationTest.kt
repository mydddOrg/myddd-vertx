package cc.lingenliu.example.document.application

import cc.lingenliu.example.document.api.DocumentApplication
import cc.lingenliu.example.document.api.DocumentDTO
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory

class DocumentApplicationTest:AbstractTest() {

    private val documentApplication by lazy { InstanceFactory.getInstance(DocumentApplication::class.java) }


    @Test
    fun testCreateDocument(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = randomCreateDocument().await()
                testContext.verify {
                    Assertions.assertNotNull(created)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testQueryByMediaId(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {
                val created = randomCreateDocument().await()

                val queryDocument = documentApplication.queryDocumentById(id = created.id).await()
                testContext.verify {
                    Assertions.assertNotNull(queryDocument)
                }

                val notExists = documentApplication.queryDocumentById(id = -1).await()
                testContext.verify {
                    Assertions.assertNull(notExists)
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    private suspend fun randomCreateDocument():Future<DocumentDTO>{
        return try {
            val documentDTO = DocumentDTO(
                mediaId = randomString(),
                name = randomString(),
                documentType = "File",
                md5 = randomString(),
                suffix = randomString()
            )

            Future.succeededFuture(documentApplication.createDocument(documentDTO).await())
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}