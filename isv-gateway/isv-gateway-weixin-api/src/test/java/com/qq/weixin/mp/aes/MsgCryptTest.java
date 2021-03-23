package com.qq.weixin.mp.aes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MsgCryptTest {

    @Test
    void testEchoHello() throws AesException {
        String sToken = "QDG6eK";
        String sCorpID = "wx5823bf96d3bd56c7";
        String sEncodingAESKey = "jWmYm7qr5nMoAUwZRjGtBxmz3KA1tkAj3ykkR6q2B2C";

        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);


        String sVerifyMsgSig = "5c45ff5e21c57e6ad56bac8758b79b1d9ac89fd3";
        String sVerifyTimeStamp = "1409659589";
        String sVerifyNonce = "263014780";
        String sVerifyEchoStr = "P9nAzCzyDtyTWESHep1vC5X9xho/qYX3Zpb4yKa9SKld1DsH3Iyt3tP3zNdtp+4RPcs8TgAE7OaBO+FZXvnaqQ==";
        String sEchoStr; //需要返回的明文
        try {
            sEchoStr = wxcpt.VerifyURL(sVerifyMsgSig, sVerifyTimeStamp,
                    sVerifyNonce, sVerifyEchoStr);
            System.out.println("verifyurl echostr: " + sEchoStr);
            Assertions.assertNotNull(sEchoStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testDecryptMsg() throws AesException {

        String sToken = "QDG6eK";
        String sCorpID = "wx5823bf96d3bd56c7";
        String sEncodingAESKey = "jWmYm7qr5nMoAUwZRjGtBxmz3KA1tkAj3ykkR6q2B2C";

        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);

        String sReqMsgSig = "477715d11cdb4164915debcba66cb864d751f3e6";
        // String sReqTimeStamp = HttpUtils.ParseUrl("timestamp");
        String sReqTimeStamp = "1409659813";
        // String sReqNonce = HttpUtils.ParseUrl("nonce");
        String sReqNonce = "1372623149";
        // post请求的密文数据
        // sReqData = HttpUtils.PostData();
        String sReqData = "<xml><ToUserName><![CDATA[wx5823bf96d3bd56c7]]></ToUserName><Encrypt><![CDATA[RypEvHKD8QQKFhvQ6QleEB4J58tiPdvo+rtK1I9qca6aM/wvqnLSV5zEPeusUiX5L5X/0lWfrf0QADHHhGd3QczcdCUpj911L3vg3W/sYYvuJTs3TUUkSUXxaccAS0qhxchrRYt66wiSpGLYL42aM6A8dTT+6k4aSknmPj48kzJs8qLjvd4Xgpue06DOdnLxAUHzM6+kDZ+HMZfJYuR+LtwGc2hgf5gsijff0ekUNXZiqATP7PF5mZxZ3Izoun1s4zG4LUMnvw2r+KqCKIw+3IQH03v+BCA9nMELNqbSf6tiWSrXJB3LAVGUcallcrw8V2t9EL4EhzJWrQUax5wLVMNS0+rUPA3k22Ncx4XXZS9o0MBH27Bo6BpNelZpS+/uh9KsNlY6bHCmJU9p8g7m3fVKn28H3KDYA5Pl/T8Z1ptDAVe0lXdQ2YoyyH2uyPIGHBZZIs2pDBS8R07+qN+E7Q==]]></Encrypt><AgentID><![CDATA[218]]></AgentID></xml>";

        try {
            String sMsg = wxcpt.DecryptMsg(sReqMsgSig, sReqTimeStamp, sReqNonce, sReqData);
            System.out.println("after decrypt msg: " + sMsg);

        } catch (Exception e) {
            // TODO
            // 解密失败，失败原因请查看异常
            e.printStackTrace();
        }
    }


    @Test
    void testDecryptMsg2() throws AesException {

        String sToken = "YLzVPx0SW7eCUl";
        String sCorpID = "wx2547800152da0539";
        String sEncodingAESKey = "5nuHy1Cg6lw5FBIxi5HVchUpEv2qnxwlYxPBTmkVQvp";

        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);

        String sReqMsgSig = "fe94dd77f4ead72c0090c1477155fd5a49a79501";
        // String sReqTimeStamp = HttpUtils.ParseUrl("timestamp");
        String sReqTimeStamp = "1616475993";
        // String sReqNonce = HttpUtils.ParseUrl("nonce");
        String sReqNonce = "1616584089";
        // post请求的密文数据
        // sReqData = HttpUtils.PostData();

        String sReqData = "<xml><ToUserName><![CDATA[wx2547800152da0539]]></ToUserName><Encrypt><![CDATA[E+UWkD1sqnWU5FNOQcm/4zQMAeSjy/GxMkmA/f0b+n++xwL5S4A9JlkJAUqfAioVUPaBfKINjY8DehZc0FODeiwtnQKuirGxp5Wo2Cgku1nzI6xU7XpH3mNm5+8tGtNVskQyq8nnHbaccWfGYjhvwYjnNE7xRDUBKB49vOEKSFdlQHrbVupEH9aaOJU779p9J+0uiUw7obXHkIIO/Jr5uNYpw/8nqVYoMYsOfcVJfUuBicA1yzBfG9UdgcQLIvZCY4baMZg4Ey/e//hGFEjlxUzaTKAMzxoBQuCXjvr6BOOQdCfakGii4UfjhNd7qM+t8i6MxhXGdTtrtagK/xd5ekbSPkSW4GyqDe2IoV/SpmEuL+yHSYgcAU/X5o3T0bXx]]></Encrypt><AgentID><![CDATA[]]></AgentID></xml>";

        try {
            String sMsg = wxcpt.DecryptMsg(sReqMsgSig, sReqTimeStamp, sReqNonce, sReqData);
            System.out.println("after decrypt msg: " + sMsg);

        } catch (Exception e) {
            // TODO
            // 解密失败，失败原因请查看异常
            e.printStackTrace();
        }
    }

    @Test
    void testEncrypt() throws AesException {
        String sToken = "QDG6eK";
        String sCorpID = "wx5823bf96d3bd56c7";
        String sEncodingAESKey = "jWmYm7qr5nMoAUwZRjGtBxmz3KA1tkAj3ykkR6q2B2C";

        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);


        // String sReqTimeStamp = HttpUtils.ParseUrl("timestamp");
        String sReqTimeStamp = "1409659813";
        // String sReqNonce = HttpUtils.ParseUrl("nonce");
        String sReqNonce = "1372623149";

        String sRespData = "<xml><ToUserName><![CDATA[mycreate]]></ToUserName><FromUserName><![CDATA[wx5823bf96d3bd56c7]]></FromUserName><CreateTime>1348831860</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[this is a test]]></Content><MsgId>1234567890123456</MsgId><AgentID>128</AgentID></xml>";
        try{
            String sEncryptMsg = wxcpt.EncryptMsg(sRespData, sReqTimeStamp, sReqNonce);
            System.out.println("after encrypt sEncrytMsg: " + sEncryptMsg);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            // 加密失败
        }
    }
}
