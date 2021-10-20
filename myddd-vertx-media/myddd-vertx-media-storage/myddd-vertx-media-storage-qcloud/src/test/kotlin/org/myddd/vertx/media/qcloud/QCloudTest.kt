package org.myddd.vertx.media.qcloud

import com.qcloud.cos.COSClient
import com.qcloud.cos.ClientConfig
import com.qcloud.cos.auth.BasicCOSCredentials
import com.qcloud.cos.http.HttpProtocol
import com.qcloud.cos.region.Region
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import com.qcloud.cos.model.PutObjectRequest
import java.io.File
import com.qcloud.cos.model.COSObjectInputStream

import com.qcloud.cos.model.COSObject

import com.qcloud.cos.model.GetObjectRequest
import org.junit.jupiter.api.Disabled


@Disabled
class QCloudTest {

    val bucketName = "isv-gateway-test-1258930758"

    private val cosClient by lazy {
        val secretId = "AKIDXopZ5LR2pa5JHEJ4fz2EAuOcaHgrhkH3"
        val secretKey = "aNtFPKxIONPAez5uTlxTklZtymIOFrBD"

        val cred = BasicCOSCredentials(secretId,secretKey)
        val region = Region("ap-guangzhou")

        val clientConfig = ClientConfig(region)
        clientConfig.httpProtocol = HttpProtocol.https

        COSClient(cred,clientConfig)
    }

    @Test
    fun testQCloud(){
        Assertions.assertNotNull(cosClient)
    }

    @Test
    fun testUpload(){
        val key = "test/my_avatar.png"
        val localFile = QCloudTest::class.java.classLoader.getResource("my_avatar.png")!!.path
        val putObjectRequest = PutObjectRequest(bucketName, key, File(localFile))
        val result = cosClient.putObject(putObjectRequest)
        Assertions.assertNotNull(result)
    }

    @Test
    fun testDownload(){
        val key = "test/my_avatar.png"
        val getObjectRequest = GetObjectRequest(bucketName, key)
        val cosObject = cosClient.getObject(getObjectRequest)
        val cosObjectInput = cosObject.objectContent

        Assertions.assertNotNull(cosObjectInput)
        cosObjectInput.close();
    }
}