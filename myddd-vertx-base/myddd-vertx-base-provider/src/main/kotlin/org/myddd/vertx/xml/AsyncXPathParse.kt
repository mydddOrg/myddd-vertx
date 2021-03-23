package org.myddd.vertx.xml

import io.vertx.core.Future
import io.vertx.core.Vertx
import org.w3c.dom.Document
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

object AsyncXPathParse {

    fun parseXml(vertx: Vertx, inputStream: InputStream): Future<Document>{
        return try {
            vertx.executeBlocking<Document> {
                val builderFactory = DocumentBuilderFactory.newInstance()
                val builder = builderFactory.newDocumentBuilder()
                it.complete(builder.parse(inputStream))
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }

    fun queryStringValue(vertx: Vertx,expression:String,document: Document): Future<String>{
        return try {
            vertx.executeBlocking<String> {
                val xPath: XPath = XPathFactory.newInstance().newXPath()
                val value = xPath.compile(expression).evaluate(document, XPathConstants.STRING) as String
                it.complete(value)
            }
        }catch (t:Throwable){
            Future.failedFuture(t)
        }
    }
}