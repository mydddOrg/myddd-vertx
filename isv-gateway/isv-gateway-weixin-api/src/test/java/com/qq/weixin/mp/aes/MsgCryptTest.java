package com.qq.weixin.mp.aes;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MsgCryptTest {

    @Test
    void testDecrypt() throws AesException {
        String sToken = "QDG6eK";
        String sCorpID = "wx5823bf96d3bd56c7";
        String sEncodingAESKey = "jWmYm7qr5nMoAUwZRjGtBxmz3KA1tkAj3ykkR6q2B2C";

        WXBizJsonMsgCrypt wxcpt = new WXBizJsonMsgCrypt(sToken, sEncodingAESKey, sCorpID);


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
    void testDecrypt2() throws AesException {

        String sToken = "QDG6eK";
        String sCorpID = "wx5823bf96d3bd56c7";
        String sEncodingAESKey = "jWmYm7qr5nMoAUwZRjGtBxmz3KA1tkAj3ykkR6q2B2C";

        WXBizJsonMsgCrypt wxcpt = new WXBizJsonMsgCrypt(sToken, sEncodingAESKey, sCorpID);

        String sReqMsgSig = "0623cbc5a8cbee5bcc137c70de99575366fc2af3";
        // String sReqTimeStamp = HttpUtils.ParseUrl("timestamp");
        String sReqTimeStamp = "1409659813";
        // String sReqNonce = HttpUtils.ParseUrl("nonce");
        String sReqNonce = "1372623149";
        // post请求的密文数据
        // sReqData = HttpUtils.PostData();
        String sReqData = "{\"tousername\":\"wx5823bf96d3bd56c7\",\"encrypt\":\"CZWs4CWRpI4VolQlvn4dlEC1alN2MUEY2VklGehgBVLBrlVF7SyT+SV+Toj43l4ayJ9UMGKphktKKmP7B2j/P1ey67XB8PBgS7Wr5/8+w/yWriZv3Vmoo/MH3/1HsIWZrPQ3N2mJrelStIfI2Y8kLKXA7EhfZgZX4o+ffdkZDM76SEl79Ib9mw7TGjZ9Aw/x/A2VjNbV1E8BtEbRxYYcQippYNw7hr8sFfa3nW1xLdxokt8QkRX83vK3DFP2F6TQFPL2Tu98UwhcUpPvdJBuu1/yiOQIScppV3eOuLWEsko=\",\"agentid\":\"218\"}";

        try {
            String sMsg = wxcpt.DecryptMsg(sReqMsgSig, sReqTimeStamp, sReqNonce, sReqData);
            System.out.println("after decrypt msg: " + sMsg);
            // TODO: 解析出明文json标签的内容进行处理
            // For example:
            JSONObject json = new JSONObject(sMsg);
            String Content = json.getString("Content");

            System.out.println("Content：" + Content);
            Assertions.assertNotNull(Content);

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

        WXBizJsonMsgCrypt wxcpt = new WXBizJsonMsgCrypt(sToken, sEncodingAESKey, sCorpID);


        // String sReqTimeStamp = HttpUtils.ParseUrl("timestamp");
        String sReqTimeStamp = "1409659813";
        // String sReqNonce = HttpUtils.ParseUrl("nonce");
        String sReqNonce = "1372623149";

        String sRespData = "{\"ToUserName\":\"wx5823bf96d3bd56c7\",\"FromUserName\":\"mycreate\",\"CreateTime\": 1409659813,\"MsgType\":\"text\",\"Content\":\"hello\",\"MsgId\":4561255354251345929,\"AgentID\": 218}";
        try{
            String sEncryptMsg = wxcpt.EncryptMsg(sRespData, sReqTimeStamp, sReqNonce);
            System.out.println("after encrypt sEncrytMsg: " + sEncryptMsg);
            // 加密成功
            // TODO:
            // HttpUtils.SetResponse(sEncryptMsg);
            Assertions.assertNotNull(sEncryptMsg);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            // 加密失败
        }
    }
}
