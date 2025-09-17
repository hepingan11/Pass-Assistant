package com.cn.bdth;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : zanglikun
 * @date : 2021/2/20 13:48
 * @Version: 1.0
 * @Desc : 费劲，没啥好说的
 */
public class pay {

    // 请求参数的含义 与 值范围，请访问呢： https://www.xunhupay.com/doc/api/pay.html
    @Test
    public void pay(){
        // appid
        String appid = "";
        // appsecret
        String appsecret = "";
        // 请求路径
        String url = "https://api.xunhupay.com/payment/do.html";
        // 设置 传递参数的集合，方便 传递数据。
        Map<String,Object> options = new HashMap<>();
        // 必填 设置版本号
        options.put("version","1.1");
        // 必填 设置 appid
        options.put("appid",appid);
        // 密钥不需要直接传递
        //options.put("appsecret",appsecret);
        // 必填 订单号 具体内容自己控制 长度 32位 官网说 请确保在当前网站内是唯一订单号，具体含义 我测试了 在描述 此备注
        options.put("trade_order_id","1321214148");
        // 必填 价格 精确到RMB分
        options.put("total_fee","0.01");
        // 必填 标题
        options.put("title","1233333");
        // 必填 当前时间戳 调用 刚写的方法 getSecondTimestamp
        options.put("time", getSecondTimestamp(new Date()));
        // 必填 通知回调地址 url 什么含义 我们后台需要知道 用户支付了。
        options.put("notify_url","http://127.0.0.1:8601/user/paycallback"); // 只有这个有用
        // 非必填 使用 响应字段中 url 就直接跳到百度了，如果访问，url_qrcode ，不会直接跳转，只有当支付完成后，再次刷新 url_qrcode中的连接，才会跳转。
        options.put("return_url","https://www.baidu.com");
        // 非必填 用户取消支付，跳转的页面   经过测试，没有触发机制，建议不传递
        options.put("callback_url","https://www.sina.com.cn/");
        //plugins 非必填 备注信息
        options.put("plugins","我是备注信息");
        // nonce_str  必填 随机值 32位内  作用: 1.避免服务器页面缓存，2.防止安全密钥被猜测出来(md5 密钥越复杂，就越难解密出来)
        options.put("nonce_str","740969606");
        // 定义 sb 为了获取 MD5 加密前的字符串
        StringBuilder sb = new StringBuilder();
        // 将HashMap 进行 键 的 Ascll 从小到大排序 并 将每个 hashmap元素 以 & 拼接起来
        options.entrySet().stream().sorted((e1,e2) -> e1.getKey().compareTo(e2.getKey())).forEach(a ->{
            sb.append(a).append("&");
        });
        // 去除 最后一位的 &
        sb.deleteCharAt(sb.length()-1);
        // 拼接上密钥
        sb.append(appsecret);
        // 调用 Hutool 的 加密工具 进行 MD5 加密
        String s = SecureUtil.md5(sb.toString());
        // 输出hash结果 postman 要用
        System.out.println("我们生成的Hash 是："+s);
        // 输出time结果 postman 要用
        System.out.println("我们生成的time 是: "+options.get("time"));
        System.out.println();
        // 必填 hash 签名
        options.put("hash", s);
        System.out.println("我们传递的参数有："+options.toString());
        System.out.println("开始调 虎皮椒支付 接口...");
        // 调用 Hutool 的HttpUtil 发送 post 请求
        String post = HttpUtil.post(url, options);
        System.out.println("结束调 虎皮椒支付 接口...\n");
        System.out.println("虎皮椒支付 接口 响应的结果是："+post+"\n");
        // 说明：这里 因为虎皮椒支付 响应结果 不统一，正确是Json;不正确 就是一行String 。没办法 判断是否请求是否有效。所以 只能通过 是由能够解析成json 有无异常判断 是否调用成功
        try{
            Map map = (Map)JSON.parse(post);
            map.keySet().stream().forEach(k -> {
                if (k == "url") {
                    System.out.println("url二维码链接是: "+map.get(k));
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("调 虎皮椒支付 时 出现了问题");
        }


    }

    /**
     * 获取精确到秒的时间戳   原理 获取毫秒时间戳，因为 1秒 = 100毫秒 去除后三位 就是秒的时间戳
     * @return
     */
    public static int getSecondTimestamp(Date date){
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime());
        int length = timestamp.length();
        if (length > 3) {
            return Integer.valueOf(timestamp.substring(0,length-3));
        } else {
            return 0;
        }
    }

    @Test
    public void test(){
        // appid
        String appid = "";
        // appsecret
        String appsecret = "";
        // 请求路径
        String url = "https://api.xunhupay.com/payment/query.html";
        // 设置 传递参数的集合，方便 传递数据。
        Map<String,Object> options = new HashMap<>();
        // 必填 设置 appid
        options.put("appid",appid);
        // 必填 订单号 具体内容自己控制 长度 32位 官网说 请确保在当前网站内是唯一订单号，具体含义 我测试了 在描述 此备注
        options.put("out_trade_order","1531812140011");
        // 必填 当前时间戳 调用 刚写的方法 getSecondTimestamp
        options.put("time", getSecondTimestamp(new Date()));
        // nonce_str  必填 随机值 32位内  作用: 1.避免服务器页面缓存，2.防止安全密钥被猜测出来(md5 密钥越复杂，就越难解密出来)
        options.put("nonce_str","740969607");
        // 定义 sb 为了获取 MD5 加密前的字符串
        StringBuilder sb = new StringBuilder();
        // 将HashMap 进行 键 的 Ascll 从小到大排序 并 将每个 hashmap元素 以 & 拼接起来
        options.entrySet().stream().sorted((e1,e2) -> e1.getKey().compareTo(e2.getKey())).forEach(a ->{
            sb.append(a).append("&");});
        // 去除 最后一位的 &
        sb.deleteCharAt(sb.length()-1);
        sb.append(appsecret);
        // 调用 Hutool 的 加密工具 进行 MD5 加密
        String s = SecureUtil.md5(sb.toString());
        // 必填 hash 签名
        options.put("hash", s);
        System.out.println("我们传递的参数有："+options);
        // 调用 Hutool 的HttpUtil 发送 post 请求
        String post = HttpUtil.post(url, options);
        System.out.println("结束调 虎皮椒支付 接口...\n");
        System.out.println("虎皮椒支付 接口 响应的结果是："+post+"\n");
        JSONObject jsonObject = JSON.parseObject(post);
        // 获取data对象
        JSONObject dataObject = jsonObject.getJSONObject("data");
        // 获取status值
        String status = dataObject.getString("status");
        System.out.println(status);
    }

}