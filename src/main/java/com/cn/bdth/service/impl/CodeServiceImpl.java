package com.cn.bdth.service.impl;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cn.bdth.dto.CodeDto;
import com.cn.bdth.entity.Code;
import com.cn.bdth.entity.CodeExchange;
import com.cn.bdth.entity.CodeImage;
import com.cn.bdth.exceptions.ExceptionMessages;
import com.cn.bdth.exceptions.OrdersException;
import com.cn.bdth.mapper.CodeExchangeMapper;
import com.cn.bdth.mapper.CodeImageMapper;
import com.cn.bdth.mapper.CodeMapper;
import com.cn.bdth.service.CodeService;
import com.cn.bdth.utils.AliUploadUtils;
import com.cn.bdth.utils.BeanUtils;
import com.cn.bdth.utils.UserUtils;
import com.cn.bdth.vo.AlipayPayCodeVo;
import com.cn.bdth.vo.CodeListVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

import static com.cn.bdth.service.impl.PayServiceImpl.getSecondTimestamp;

@Service
@Slf4j
@RequiredArgsConstructor
public class CodeServiceImpl extends ServiceImpl<CodeMapper, Code> implements CodeService {

    private final CodeMapper codeMapper;

    private final CodeImageMapper codeImageMapper;

    private final AliUploadUtils aliUploadUtils;

    private final CodeExchangeMapper codeExchangeMapper;


    /**
     * 查询列表
     * @param name
     * @return
     */
    @Override
    public List<CodeListVo> codelist(String name, String language) {

        QueryWrapper<Code> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Code::getIsPublic, 1);
        if (name != null && !name.isEmpty()) {
            queryWrapper.lambda().like(Code::getCodeName, name);
        }

        if (language != null && !language.isEmpty()) {
            queryWrapper.lambda().eq(Code::getLanguage, language);
        }

        List<Code> codeList = codeMapper.selectList(queryWrapper);
        return getCodeListVos(codeList);
    }

    /**
     * 添加作品
     * @param codeDto
     */
    @Override
    public void addCode(CodeDto codeDto) {
        Code code =new Code();
        code.setCodeName(codeDto.getCodeName());
        code.setUserId(UserUtils.getCurrentLoginId());
        code.setLook(0);
        code.setIntroduce(codeDto.getIntroduce());
        code.setPrice(codeDto.getPrice());
        code.setDownloadUrl(codeDto.getDownloadUrl());
        code.setLanguage(codeDto.getLanguage());
        code.setIsPublic(0);
        codeMapper.insert(code);
        for (MultipartFile image : codeDto.getImageList()) {
            CodeImage codeImage = new CodeImage();
            codeImage.setCodeId(code.getCodeId());
            final String fileName = UUID.randomUUID() + ".jpg";
            String imgUrl = aliUploadUtils.uploadFile(image, "code", fileName, true);
            codeImage.setImageUrl(imgUrl);
            codeImageMapper.insert(codeImage);
        }
        ;
    }

    /**
     * 增加点击量
     * @param codeId
     */
    @Override
    public void addLook(Long codeId) {
        Code code = codeMapper.selectById(codeId);
        code.setLook(code.getLook()+1);
        codeMapper.updateById(code);
    }

    @Override
    public void orderBuy(Long codeId) {
        Long userId = UserUtils.getCurrentLoginId();
        Code code = codeMapper.selectById(codeId);
        CodeExchange codeExchange = new CodeExchange();
        codeExchange.setCodeId(codeId);
        codeExchange.setBuyUserId(userId);
        codeExchange.setSellUserId(code.getUserId());
        codeExchange.setPrice(code.getPrice());
        codeExchange.setCreatedTime(LocalDateTime.now());
        codeExchangeMapper.insert(codeExchange);
    }

    @Value("${hupijiao.appid}")
    private String AppId;

    @Value("${hupijiao.appsecret}")
    private String AppSecret;

    @Value("${hupijiao.webUrl}")
    private String webUrl;

    @Override
    public AlipayPayCodeVo generatePayQrCode(Long codeId) {
        Code code = codeMapper.selectOne(new QueryWrapper<Code>().lambda().eq(Code::getCodeId, codeId));
        if (code == null) {
            throw new OrdersException(ExceptionMessages.PRODUCT_NULL_ERR, 500);
        }
        // 请求路径
        String url = "https://api.xunhupay.com/payment/do.html";
        // 设置 传递参数的集合，方便 传递数据。
        Map<String,Object> options = new HashMap<>();
        // 必填 设置版本号
        options.put("version","1.1");
        // 必填 设置 appid
        options.put("appid",AppId);
        // 必填 订单号 具体内容自己控制 长度 32位
        // 官网说 请确保在当前网站内是唯一订单号，具体含义 我测试了 在描述 此备注
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            stringBuilder.append(secureRandom.nextInt(12)); // 生成0-9之间的随机数
        }
        String randomNumber = stringBuilder.toString();
        options.put("trade_order_id",randomNumber);
        // 必填 价格 精确到RMB分
        String formattedNumber = String.format("%.2f", code.getPrice());
        options.put("total_fee",formattedNumber);
        // 必填 标题
        options.put("title",code.getCodeName());
        // 必填 当前时间戳 调用 刚写的方法 getSecondTimestamp
        options.put("time", getSecondTimestamp(new Date()));
        // 必填 通知回调地址 url 什么含义 我们后台需要知道 用户支付了。
        options.put("notify_url","https://java.hepingan.top/pay/paycallback"); // 只有这个有用
        // 非必填 使用 响应字段中 url 就直接跳到百度了，如果访问，url_qrcode ，不会直接跳转，只有当支付完成后，再次刷新 url_qrcode中的连接，才会跳转。
        options.put("return_url",webUrl+"/#/my_code");
        // 非必填 用户取消支付，跳转的页面   经过测试，没有触发机制，建议不传递
        options.put("callback_url",webUrl+"/#/my_code");
        //plugins 非必填 备注信息
        options.put("plugins","源码市场源码购买，codeId:"+codeId);
        // nonce_str  必填 随机值 32位内  作用: 1.避免服务器页面缓存，2.防止安全密钥被猜测出来(md5 密钥越复杂，就越难解密出来)
        SecureRandom secureRandom2 = new SecureRandom();
        StringBuilder stringBuilder2 = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            stringBuilder2.append(secureRandom2.nextInt(10)); // 生成0-9之间的随机数
        }
        String randomNumber2 = stringBuilder2.toString();
        options.put("nonce_str",randomNumber2);
        // 定义 sb 为了获取 MD5 加密前的字符串
        StringBuilder sb = new StringBuilder();
        // 将HashMap 进行 键 的 Ascll 从小到大排序 并 将每个 hashmap元素 以 & 拼接起来
        options.entrySet().stream().sorted((e1,e2) -> e1.getKey().compareTo(e2.getKey())).forEach(a ->{
            sb.append(a).append("&");});
        // 去除 最后一位的 &
        sb.deleteCharAt(sb.length()-1);
        // 拼接上密钥
        sb.append(AppSecret);
        // 调用 Hutool 的 加密工具 进行 MD5 加密
        String s = SecureUtil.md5(sb.toString());
        // 必填 hash 签名
        options.put("hash", s);
        // 调用 Hutool 的HttpUtil 发送 post 请求
        String post = HttpUtil.post(url, options);
        // 说明：这里 因为虎皮椒支付 响应结果 不统一，正确是Json;
        // 不正确 就是一行String 。没办法 判断是否请求是否有效。
        // 所以 只能通过 是由能够解析成json 有无异常判断 是否调用成功
        try{
            Map<String, Object> map = (Map<String, Object>) JSON.parse(post);
            String payUrl = (String) map.get("url");
            LocalDateTime createdTime = LocalDateTime.now();
            CodeExchange codeExchange = new CodeExchange();
            codeExchange.setOrderNo(randomNumber);
            codeExchange.setCodeId(codeId);
            codeExchange.setBuyUserId(UserUtils.getCurrentLoginId());
            codeExchange.setSellUserId(code.getUserId());
            codeExchange.setPrice(code.getPrice());
            codeExchange.setCreatedTime(createdTime);
            codeExchange.setStatus("待支付");
            codeExchangeMapper.insert(codeExchange);
            return new AlipayPayCodeVo()
                    .setOrdersId(randomNumber)
                    .setCreatedTime(createdTime)
                    .setProductName(code.getCodeName())
                    .setProductPrice(code.getPrice())
                    .setPayUrl(payUrl);
        }catch (OrdersException e){
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public String paymentStatus(String orderNo) {
        // 请求路径
        String url = "https://api.xunhupay.com/payment/query.html";
        // 设置 传递参数的集合，方便 传递数据。
        Map<String,Object> options = new HashMap<>();
        // 必填 设置 appid
        options.put("appid",AppId);
        // 必填 订单号 具体内容自己控制 长度 32位 官网说 请确保在当前网站内是唯一订单号，具体含义 我测试了 在描述 此备注
        options.put("out_trade_order",orderNo);
        // 必填 当前时间戳 调用 刚写的方法 getSecondTimestamp
        options.put("time", getSecondTimestamp(new Date()));
        // nonce_str  必填 随机值 32位内  作用: 1.避免服务器页面缓存，2.防止安全密钥被猜测出来(md5 密钥越复杂，就越难解密出来)
        SecureRandom secureRandom2 = new SecureRandom();
        StringBuilder stringBuilder2 = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            stringBuilder2.append(secureRandom2.nextInt(10)); // 生成0-9之间的随机数
        }
        String randomNumber2 = stringBuilder2.toString();
        options.put("nonce_str",randomNumber2);
        // 定义 sb 为了获取 MD5 加密前的字符串
        StringBuilder sb = new StringBuilder();
        // 将HashMap 进行 键 的 Ascll 从小到大排序 并 将每个 hashmap元素 以 & 拼接起来
        options.entrySet().stream().sorted((e1,e2) -> e1.getKey().compareTo(e2.getKey())).forEach(a ->{
            sb.append(a).append("&");});
        // 去除 最后一位的 &
        sb.deleteCharAt(sb.length()-1);
        sb.append(AppSecret);
        // 调用 Hutool 的 加密工具 进行 MD5 加密
        String s = SecureUtil.md5(sb.toString());
        // 必填 hash 签名
        options.put("hash", s);
        // 调用 Hutool 的HttpUtil 发送 post 请求
        String post = HttpUtil.post(url, options);

        JSONObject jsonObject = JSON.parseObject(post);
        // 获取data对象
        JSONObject dataObject = jsonObject.getJSONObject("data");
        // 获取status值
        String status = dataObject.getString("status");
        if (Objects.equals(status, "OD")){
            CodeExchange codeExchange = codeExchangeMapper.selectOne(new QueryWrapper<CodeExchange>().lambda().eq(CodeExchange::getOrderNo, orderNo));
            codeExchange.setStatus("已支付");
            codeExchangeMapper.updateById(codeExchange);
            return "success";
        }else if (Objects.equals(status, "WP")){
            return "wait";
        }else {
            codeExchangeMapper.delete(new QueryWrapper<CodeExchange>().lambda().eq(CodeExchange::getOrderNo, orderNo));
            return "cancel";
        }
    }

    @Override
    public void updateCode(CodeDto codeDto) {
        Code code = codeMapper.selectById(codeDto.getCodeId());
        code.setCodeName(codeDto.getCodeName());
        code.setIntroduce(codeDto.getIntroduce());
        code.setLanguage(codeDto.getLanguage());
        code.setIsPublic(0);
        code.setPrice(codeDto.getPrice());
        code.setDownloadUrl(codeDto.getDownloadUrl());
        code.setUpdateTime(LocalDateTime.now());
        codeMapper.updateById(code);
    }

    @Override
    public void updateCodeImage(Long codeId, MultipartFile image) {
        final String fileName = UUID.randomUUID() + ".jpg";
        String imgUrl = aliUploadUtils.uploadFile(image, "code", fileName, true);
        CodeImage codeImage = new CodeImage();
        codeImage.setCodeId(codeId);
        codeImage.setImageUrl(imgUrl);
        codeImageMapper.insert(codeImage);
    }

    @Override
    public void deleteCodeImage(String imageUrl) {
        aliUploadUtils.deleteFile(imageUrl);
        codeImageMapper.delete(new QueryWrapper<CodeImage>().lambda().eq(CodeImage::getImageUrl, imageUrl));
    }

    @Override
    public void passCode(Long id) {
        Code code = codeMapper.selectById(id);
        code.setIsPublic(1);
        codeMapper.updateById(code);
    }

    @Override
    public void rejectCode(Long id) {
        Code code = codeMapper.selectById(id);
        code.setIsPublic(0);
        codeMapper.updateById(code);
    }

    @Override
    public List<CodeListVo> allCode() {
        List<Code> codeList = codeMapper.selectList(new QueryWrapper<Code>().lambda().orderByDesc(Code::getCreatedTime));
        return getCodeListVos(codeList);
    }

    @Override
    public List<CodeListVo> myCode() {
        List<Code> codeList = codeMapper.selectList(new QueryWrapper<Code>().lambda().eq(Code::getUserId, UserUtils.getCurrentLoginId()));
        return getCodeListVos(codeList);
    }

    @NotNull
    private List<CodeListVo> getCodeListVos(List<Code> codeList) {
        List<CodeListVo> codeListVoList = new ArrayList<>();

        for (Code code : codeList) {
            List<CodeImage> codeImages = codeImageMapper.selectList(new QueryWrapper<CodeImage>().lambda()
                    .eq(CodeImage::getCodeId, code.getCodeId())
                    .select(CodeImage::getImageUrl));
            List<String> imageList = new ArrayList<>();
            for (CodeImage codeImage : codeImages) {
                imageList.add(codeImage.getImageUrl());
            }
            CodeListVo codeListVo = BeanUtils.copyClassProperTies(code, CodeListVo.class);
            codeListVo.setImageList(imageList);
            codeListVoList.add(codeListVo);
        }

        return codeListVoList;
    }
}
