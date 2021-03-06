package org.myddd.vertx.xml

import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.AbstractTest
import org.myddd.vertx.junit.execute

class AsyncXPathParseTest:AbstractTest() {

    companion object {
        private const val XML_CONTENT = "<xml><ToUserName><![CDATA[wx2547800152da0539]]></ToUserName><Encrypt><![CDATA[D6G7ZsYPTBlM05ZaDgtKaiLULJ9Rp1RHG+oaPKEiur5pM8Zmh7YdEccw/tiHC1q8rP6/7XPDzrR4jP3mPFUZpZINLeeekDx9SooYlt7/NgYDgDzLTriEZXx9w9fHnkovPPnwpSjJU0aTSTbEi0k9s8TZ8RXSbYc064V5lujgEv/ePllgn7umSMJspY+E4abf8iUSVKv+bwi4ACHfUj25gBu1w0eLsWWiDb4PaOfVthsk+0EEdbRlq/PJSHiWCjCpfCBD7VNsZ+FsOqYceLL4ppMdJItgbN8C/64ehzMC4sSIdlVonSepZ7QUjAMvCTcmTmVXRJQm1fUpIZoRY31N8iDlxNtSpjNmwx+ds+YzEKb4maxFPS758DSmljWrnqc5]]></Encrypt><AgentID><![CDATA[]]></AgentID></xml>"
    }

    @Test
    fun testQueryDocument(testContext: VertxTestContext){
        testContext.execute {
            val document = AsyncXPathParse.parseXml(vertx, XML_CONTENT.byteInputStream()).await()
            testContext.verify { Assertions.assertNotNull(document) }
        }
    }

    @Test
    fun testParseValue(vertx: Vertx,testContext: VertxTestContext){
        testContext.execute {
            val document = AsyncXPathParse.parseXml(vertx, XML_CONTENT.byteInputStream()).await()
            val encrypt = AsyncXPathParse.queryStringValue(vertx = vertx,document = document,expression = "/xml/Encrypt").await()
            println(encrypt)
            testContext.verify {
                Assertions.assertNotNull(encrypt)
            }
        }
    }
}