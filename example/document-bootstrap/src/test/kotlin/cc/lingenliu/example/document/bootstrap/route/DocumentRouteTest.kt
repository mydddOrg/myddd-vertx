package cc.lingenliu.example.document.bootstrap.route

import cc.lingenliu.example.document.bootstrap.AbstractRouteTest
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DocumentRouteTest:AbstractRouteTest() {



    @Test
    fun testCreateDocumentRoute(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {

                val requestJson = json {
                    obj(
                        "mediaId" to randomString(),
                        "name" to randomString(),
                        "documentType" to "File",
                        "md5" to randomString(),
                        "suffix" to randomString()
                    )
                }

                val response = webClient.post(port,host,"/v1/documents")
                    .sendJson(requestJson)
                    .await()
                testContext.verify {
                    Assertions.assertEquals(200,response.statusCode())
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }

    @Test
    fun testQueryDocumentRoute(vertx: Vertx,testContext: VertxTestContext){
        GlobalScope.launch(vertx.dispatcher()) {
            try {

                val errorResponse = webClient.get(port,host,"/v1/documents/-1")
                    .send()
                    .await()
                testContext.verify {
                    Assertions.assertEquals(400,errorResponse.statusCode())
                    logger.debug(errorResponse.bodyAsString())
                }

                val requestJson = json {
                    obj(
                        "mediaId" to randomString(),
                        "name" to randomString(),
                        "documentType" to "File",
                        "md5" to randomString(),
                        "suffix" to randomString()
                    )
                }

                val response = webClient.post(port,host,"/v1/documents")
                    .sendJson(requestJson)
                    .await()
                val body = response.bodyAsJsonObject()
                val id = body.getString("id")

                val queryResponse = webClient.get(port,host,"/v1/documents/$id")
                    .send()
                    .await()

                testContext.verify {
                    Assertions.assertEquals(200,queryResponse.statusCode())
                }
            }catch (t:Throwable){
                testContext.failNow(t)
            }
            testContext.completeNow()
        }
    }
}