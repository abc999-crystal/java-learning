package springboot.wxcommon.handler;

import cn.hutool.core.util.IdUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class SmsHandle {
    private static final String WSSE_HEADER_FORMAT = "UsernameToken Username=\"%s\",PasswordDigest=\"%s\",Nonce=\"%s\",Created=\"%s\"";

    private static final String AUTH_HEADER_VALUE = "WSSE realm=\"SDP\",profile=\"UsernameToken\",type=\"Appkey\"";

    // TODO 请从应用管理页面获取APP接入地址，替换url中的ip地址和端口
    @Value("${sms.api:0}")
    String url = "https://rtcsms.cn-north-1.myhuaweicloud.com:10743/sms/batchSendSms/v1";
    // TODO 请从应用管理页面获取APP_Key和APP_Secret进行替换
    @Value("${sms.appKey:0}")
    String appKey = "qyy47Z2SRWBDIvmmYot3m8f7qPn4";
    @Value("${sms.appSecret:0}")
    String appSecret = "TN21N4rJW1JyAE92H26gDT5Qiiac";

    // 填写短信签名中的通道号，请从签名管理页面获取
    @Value("${sms.captcha.sender:0}")
    String sender = "8822061734779";
    //状态报告接收地址，为空或者不填表示不接收状态报告
    @Value("${sms.captcha.statusCallBack:0}")
    String statusCallBack = "";

    /*
    验证码类
    ID：49f23235590d46b7bcf33bf2c5bfd032
    内容：验证码为：${1}，请勿向其他人提供此信息。
     */
    // TODO 请从模板管理页面获取模板ID进行替换
    @Value("${sms.captcha.templateId:0}")
    String templateId = "49f23235590d46b7bcf33bf2c5bfd032";

    static String buildRequestBody(String sender, String receiver, String templateId, String templateParas,
                                   String statusCallbackUrl) {

        List<NameValuePair> keyValues = new ArrayList<>();

        keyValues.add(new BasicNameValuePair("from", sender));
        keyValues.add(new BasicNameValuePair("to", receiver));
        keyValues.add(new BasicNameValuePair("templateId", templateId));
        keyValues.add(new BasicNameValuePair("templateParas", templateParas));
        keyValues.add(new BasicNameValuePair("statusCallback", statusCallbackUrl));

        return URLEncodedUtils.format(keyValues, StandardCharsets.UTF_8);
    }

    static String buildWsseHeader(String appKey, String appSecret) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String time = sdf.format(new Date());
        String nonce = IdUtil.fastSimpleUUID();

        byte[] passwordDigest = DigestUtils.sha256(nonce + time + appSecret);
        String hexDigest = Hex.encodeHexString(passwordDigest);
        String passwordDigestBase64Str = Base64.encodeBase64String(hexDigest.getBytes(StandardCharsets.UTF_8));
        return String.format(WSSE_HEADER_FORMAT, appKey, passwordDigestBase64Str, nonce, time);
    }

    public void sendSms(String mobile, String content) throws Exception {

        String templateParas = "[\"" + content + "\"]";

        String body = buildRequestBody(sender, mobile, templateId, templateParas, statusCallBack);
        System.out.println("body is " + body);

        String wsseHeader = buildWsseHeader(appKey, appSecret);
        System.out.println("wsse header is " + wsseHeader);

        CloseableHttpClient client = HttpClients.custom()
                .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null,
                        (x509CertChain, authType) -> true).build())
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();
        HttpResponse response = client.execute(RequestBuilder.create("POST")
                .setUri(url)
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .addHeader(HttpHeaders.AUTHORIZATION, AUTH_HEADER_VALUE)
                .addHeader("X-WSSE", wsseHeader)
                .setEntity(new StringEntity(body)).build());

        System.out.println(response.toString());
        System.out.println(EntityUtils.toString(response.getEntity()));

    }

}
