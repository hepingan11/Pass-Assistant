import com.alibaba.fastjson.JSONObject;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import java.io.FileOutputStream;
import java.io.IOException;

import java.util.*;



@SpringBootTest
@RunWith(SpringRunner.class)
public class springtest {

    @Test
    public void test1() throws IOException {
        System.out.println("hello");
    }

    @Test
    public void test() {
        String endpoint = "http://sd.hepingan.top";
        String username = "";
        String password = "";

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(endpoint + "/sdapi/v1/txt2img");

        try {
            String credentials = username + ":" + password;
            String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes());
            request.setHeader("Authorization", "Basic " + base64Credentials);

            String json = "{" +
                    "\"prompt\": \"lovely girly,avatar\"," +
                    "\"step\": 10," +
                    "\"height\": 512," +
                    "\"width\": 512," +
                    "\"override_settings\": {" +
                    "\"sd_model_checkpoint\": \"mixProV4.Cqhm.safetensors\"" +
                    "}" +
                    "}";
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            request.setEntity(entity);

            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            if (response.getStatusLine().getStatusCode() == 200) {
                if (responseEntity != null) {
                    String responseJson = EntityUtils.toString(responseEntity);
                    // 解析响应的图片数据并保存
                    // 注意使用不同的命名方式来保存不同的图片，这里默认保存为4.png
                    FileOutputStream fileOutputStream = new FileOutputStream("4.png");
                    String base64Image = responseJson.substring(responseJson.indexOf("\"images\"") + 10, responseJson.length()-3);
                    byte[] imageBytes = Base64.getDecoder().decode(base64Image.getBytes());

                    fileOutputStream.write(imageBytes);
                    fileOutputStream.close();

                    // 打印响应结果
                    System.out.println(responseJson);
                }
            } else {
                System.out.println(response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void processImages() throws IOException {

        CloseableHttpClient aDefault = HttpClients.createDefault();

        HttpPost httpPost =new HttpPost("https://sd.hepingan.top/sdapi/v1/txt2img");
        JSONObject param = new JSONObject();
        param.put("prompt", "girl");
        param.put("step",10);
        param.put("height",512);
        param.put("width",512);
        param.put("override_settings",new JSONObject().put("sd_model_checkpoint","majicMIX realistic_v6.safetensors"));

        StringEntity stringEntity=new StringEntity(param.toString());
        Header header=new BasicHeader("Content-Type","application/json");
        httpPost.setHeader(header);
        httpPost.setHeader("Authorization","Basic Og== ");
        httpPost.setHeader("Connection","keep-alive");
        httpPost.setEntity(stringEntity);
        CloseableHttpResponse execute = aDefault.execute(httpPost);

        String s = EntityUtils.toString(execute.getEntity());
        System.out.println("结果为:"+s);

    }

    @Test
    public void processImages2() throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://node1:8020");
        // 如果需要的话，设置用户
        System.setProperty("HADOOP_USER_NAME", "hadoop");
        FileSystem fs = FileSystem.get(conf);
        // 接下来进行文件操作
        Path path = new Path("D:/Photo/4/1.png");
        Path dst = new Path("/test.txt");
        fs.copyFromLocalFile(path,dst);
        fs.close();

    }

    @Test
    public void processImages1() throws IOException {
        System.out.println(1);
    }

    @Test
    public void processImages3() {
        String vmid = "443081814";
        String apiUrl = "https://api.bilibili.com/x/relation/stat?vmid=" + vmid;

        CloseableHttpClient aDefault = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(apiUrl);
        CloseableHttpResponse execute;
        try {
            execute = aDefault.execute(httpGet);
            System.out.println(execute);
//            String responseContent = EntityUtils.toString(execute.getEntity(), "UTF-8");
//            System.out.println(responseContent);
        }catch (IOException e){
            throw new RuntimeException(e);
        }

    }


}