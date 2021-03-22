package com.foreverht.isvgateway.bootstrap.weixin

import com.qq.weixin.mp.aes.WXBizJsonMsgCrypt
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class WeiXinApiTest {

    companion object {
        private const val sToken = "QDG6eK"
        private const val sCorpID = "wx5823bf96d3bd56c7"
        private const val sEncodingAESKey = "jWmYm7qr5nMoAUwZRjGtBxmz3KA1tkAj3ykkR6q2B2C"

        private lateinit var wxcpt: WXBizJsonMsgCrypt

        @BeforeAll
        @JvmStatic
        fun beforeAll(){
            wxcpt = WXBizJsonMsgCrypt(sToken, sEncodingAESKey, sCorpID)
            Assertions.assertNotNull(wxcpt)
        }
    }

    @Test
    fun testDecodeCallback(){
        val sVerifyMsgSig = "5c45ff5e21c57e6ad56bac8758b79b1d9ac89fd3"
        val sVerifyTimeStamp = "1409659589"
        val sVerifyNonce = "263014780"
        val sVerifyEchoStr = "P9nAzCzyDtyTWESHep1vC5X9xho/qYX3Zpb4yKa9SKld1DsH3Iyt3tP3zNdtp+4RPcs8TgAE7OaBO+FZXvnaqQ=="

        val sEchoStr = wxcpt.VerifyURL(sVerifyMsgSig, sVerifyTimeStamp,sVerifyNonce, sVerifyEchoStr)

        Assertions.assertNotNull(sEchoStr)
    }

    @Test
    fun testDecodeMsgContent(){
        val sReqMsgSig = "0623cbc5a8cbee5bcc137c70de99575366fc2af3"
        val sReqTimeStamp = "1409659813"
        val sReqNonce = "1372623149"
        val sReqData =
            "{\"tousername\":\"wx5823bf96d3bd56c7\",\"encrypt\":\"CZWs4CWRpI4VolQlvn4dlEC1alN2MUEY2VklGehgBVLBrlVF7SyT+SV+Toj43l4ayJ9UMGKphktKKmP7B2j/P1ey67XB8PBgS7Wr5/8+w/yWriZv3Vmoo/MH3/1HsIWZrPQ3N2mJrelStIfI2Y8kLKXA7EhfZgZX4o+ffdkZDM76SEl79Ib9mw7TGjZ9Aw/x/A2VjNbV1E8BtEbRxYYcQippYNw7hr8sFfa3nW1xLdxokt8QkRX83vK3DFP2F6TQFPL2Tu98UwhcUpPvdJBuu1/yiOQIScppV3eOuLWEsko=\",\"agentid\":\"218\"}"

        val sMsg = wxcpt.DecryptMsg(sReqMsgSig, sReqTimeStamp, sReqNonce, sReqData)

        println(sMsg)
        Assertions.assertNotNull(sMsg)
    }

    @Test
    fun testEncryptContent(){
        val sReqTimeStamp = "1409659813"
        val sReqNonce = "1372623149"
        val sRespData =
            "{\"ToUserName\":\"wx5823bf96d3bd56c7\",\"FromUserName\":\"mycreate\",\"CreateTime\": 1409659813,\"MsgType\":\"text\",\"Content\":\"hello\",\"MsgId\":4561255354251345929,\"AgentID\": 218}"

        val sEncryptMsg = wxcpt.EncryptMsg(sRespData, sReqTimeStamp, sReqNonce);
        Assertions.assertNotNull(sEncryptMsg)
    }
}