import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.*;

import static com.cn.bdth.service.impl.DataServiceImpl.findTopFrequentWords;


public class HanlpTest {


    public static void main(String[] args) {
        String vmid = "443081814";
        String apiUrl = "https://api.bilibili.com/x/relation/stat?vmid=" + vmid;

        CloseableHttpClient aDefault = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(apiUrl);
        CloseableHttpResponse execute;
        try {
            execute = aDefault.execute(httpGet);
            System.out.println(execute);
            String responseContent = EntityUtils.toString(execute.getEntity(), "UTF-8");
            System.out.println(responseContent);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
