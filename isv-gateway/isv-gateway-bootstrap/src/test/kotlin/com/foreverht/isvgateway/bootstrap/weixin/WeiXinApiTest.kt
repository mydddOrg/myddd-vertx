package com.foreverht.isvgateway.bootstrap.weixin

import com.qq.weixin.mp.aes.WXBizMsgCrypt
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class WeiXinApiTest {

    companion object {
        private const val sToken = "QDG6eK"
        private const val sCorpID = "wx5823bf96d3bd56c7"
        private const val sEncodingAESKey = "jWmYm7qr5nMoAUwZRjGtBxmz3KA1tkAj3ykkR6q2B2C"

        private lateinit var wxcpt: WXBizMsgCrypt

        @BeforeAll
        @JvmStatic
        fun beforeAll(){
            wxcpt = WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID)
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
        val sReqMsgSig = "477715d11cdb4164915debcba66cb864d751f3e6"
        val sReqTimeStamp = "1409659813"
        val sReqNonce = "1372623149"
        // post请求的密文数据
        // sReqData = HttpUtils.PostData();
        val sReqData =
            "<xml><ToUserName><![CDATA[wx5823bf96d3bd56c7]]></ToUserName><Encrypt><![CDATA[RypEvHKD8QQKFhvQ6QleEB4J58tiPdvo+rtK1I9qca6aM/wvqnLSV5zEPeusUiX5L5X/0lWfrf0QADHHhGd3QczcdCUpj911L3vg3W/sYYvuJTs3TUUkSUXxaccAS0qhxchrRYt66wiSpGLYL42aM6A8dTT+6k4aSknmPj48kzJs8qLjvd4Xgpue06DOdnLxAUHzM6+kDZ+HMZfJYuR+LtwGc2hgf5gsijff0ekUNXZiqATP7PF5mZxZ3Izoun1s4zG4LUMnvw2r+KqCKIw+3IQH03v+BCA9nMELNqbSf6tiWSrXJB3LAVGUcallcrw8V2t9EL4EhzJWrQUax5wLVMNS0+rUPA3k22Ncx4XXZS9o0MBH27Bo6BpNelZpS+/uh9KsNlY6bHCmJU9p8g7m3fVKn28H3KDYA5Pl/T8Z1ptDAVe0lXdQ2YoyyH2uyPIGHBZZIs2pDBS8R07+qN+E7Q==]]></Encrypt><AgentID><![CDATA[218]]></AgentID></xml>"

        val sMsg = wxcpt.DecryptMsg(sReqMsgSig, sReqTimeStamp, sReqNonce, sReqData)

        Assertions.assertNotNull(sMsg)
    }

    @Test
    fun testEncryptContent(){
        val sReqTimeStamp = "1409659813"
        val sReqNonce = "1372623149"
        // post请求的密文数据
        // sReqData = HttpUtils.PostData();
        val sReqData =
            "<xml><ToUserName><![CDATA[wx2547800152da0539]]></ToUserName><Encrypt><![CDATA[E+UWkD1sqnWU5FNOQcm/4zQMAeSjy/GxMkmA/f0b+n++xwL5S4A9JlkJAUqfAioVUPaBfKINjY8DehZc0FODeiwtnQKuirGxp5Wo2Cgku1nzI6xU7XpH3mNm5+8tGtNVskQyq8nnHbaccWfGYjhvwYjnNE7xRDUBKB49vOEKSFdlQHrbVupEH9aaOJU779p9J+0uiUw7obXHkIIO/Jr5uNYpw/8nqVYoMYsOfcVJfUuBicA1yzBfG9UdgcQLIvZCY4baMZg4Ey/e//hGFEjlxUzaTKAMzxoBQuCXjvr6BOOQdCfakGii4UfjhNd7qM+t8i6MxhXGdTtrtagK/xd5ekbSPkSW4GyqDe2IoV/SpmEuL+yHSYgcAU/X5o3T0bXx]]></Encrypt><AgentID><![CDATA[]]></AgentID></xml>"

        val sEncryptMsg = wxcpt.EncryptMsg(sReqData, sReqTimeStamp, sReqNonce);
        Assertions.assertNotNull(sEncryptMsg)
    }
}