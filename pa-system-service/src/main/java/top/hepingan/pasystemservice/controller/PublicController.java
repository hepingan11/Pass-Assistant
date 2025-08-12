package top.hepingan.pasystemservice.controller;

import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.hepingan.pacommon.msg.Result;

import java.io.IOException;


/**
 * 公开
 *
 * @author  @github dulaiduwang003
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {


    @GetMapping(value = "/bilibili/fan",name = "获取b站粉丝")
    public Result getBilibiliFan(){
        return Result.data(0);
//        String vmid = "443081814";
//        String apiUrl = "https://api.bilibili.com/x/relation/stat?vmid=" + vmid;
//
//        CloseableHttpClient aDefault = HttpClients.createDefault();
//
//        HttpGet httpGet = new HttpGet(apiUrl);
//        CloseableHttpResponse execute;
//        try {
//            execute = aDefault.execute(httpGet);
//            String responseContent = EntityUtils.toString(execute.getEntity(), "UTF-8");
//            JSONObject jsonObject = JSONObject.parseObject(responseContent);
//            int follower = jsonObject.getJSONObject("data").getIntValue("follower");
//            return Result.data(follower);
//        }catch (IOException e){
//            throw new RuntimeException(e);
//        }
    }
}
