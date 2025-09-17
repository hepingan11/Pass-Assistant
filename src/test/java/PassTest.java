import com.alibaba.fastjson.JSONObject;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class PassTest {

    @Test
    public void test() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 设置请求URL
        String url = "https://www.paisi.edu.cn:8181/jsxsd/";
        Header header=new BasicHeader("Content-Type","application/x-www-form-urlencoded");
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader(header);
        httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0");

        // 执行请求
        HttpResponse response = httpClient.execute(httpGet);

        // 获取响应状态码
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("Response status: " + statusCode);

        // 从响应中获取Cookie
        // 注意这里简化了Cookie的处理，实际应用中可能需要更复杂的逻辑来解析和管理Cookie
        // 通常可以通过CookieStore来管理和使用Cookie
        // 这里仅作为示例展示如何获取Cookie头
        String cookieHeader = response.getFirstHeader("Set-Cookie").getValue();
        System.out.println("Received cookies: " + cookieHeader);

        HttpPost httpPost = new HttpPost("https://www.paisi.edu.cn:8181/jsxsd/xk/LoginToXk");
        httpPost.setHeader("Cookie",cookieHeader);
        String account = "2022126002";
        String password = "123Cd233";
        String encoded = encodeAccountAndPassword(account, password);
        JSONObject param = new JSONObject();
        param.put("userAccount",account);
        param.put("userPassword",password);
        param.put("encoded",encoded);
        param.put("pwdstr1","");
        param.put("pwdstr2","");

        StringEntity stringEntity= null;
        try {
            stringEntity = new StringEntity(param.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        httpPost.setEntity(stringEntity);
        HttpResponse response2 = httpClient.execute(httpPost);

        HttpEntity responseEntity = response2.getEntity();
        if (responseEntity != null) {
            String responseBody = EntityUtils.toString(responseEntity, "UTF-8");
            System.out.println("Response body: " + responseBody);
        }

        // 关闭连接
        EntityUtils.consume(responseEntity);

        HttpPost httpPost1 = new HttpPost("https://www.paisi.edu.cn:8181/jsxsd/xskb/xskb_list.do");
        httpPost1.setHeader("Cookie",cookieHeader);
        httpPost1.setHeader(header);
        httpPost1.setHeader("Referer","https://www.paisi.edu.cn:8181/jsxsd/framework/xsMain.jsp");
        HttpResponse response3 = httpClient.execute(httpPost);
        HttpEntity responseEntity1 = response3.getEntity();
        if (responseEntity1 != null) {
            String responseBody = EntityUtils.toString(responseEntity1, "UTF-8");
            System.out.println("Response body: " + responseBody);
        }
        EntityUtils.consume(responseEntity1);
    }



    public static String encodeAccountAndPassword(String account, String password) {
        byte[] accountBytes = account.getBytes(StandardCharsets.UTF_8);
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);

        String encodedAccount = Base64.getEncoder().encodeToString(accountBytes);
        String encodedPassword = Base64.getEncoder().encodeToString(passwordBytes);

        return encodedAccount + "%%%" + encodedPassword;
    }

}
