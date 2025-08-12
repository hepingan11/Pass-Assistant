package top.hepingan.pasystemservice.service.impl;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.hepingan.pacommon.constants.OrderConstant;
import top.hepingan.pacommon.dto.ShelvesProductDto;
import top.hepingan.pacommon.entity.Orders;
import top.hepingan.pacommon.entity.Product;
import top.hepingan.pacommon.entity.User;
import top.hepingan.pacommon.exceptions.ExceptionMessages;
import top.hepingan.pacommon.exceptions.OrdersException;
import top.hepingan.pacommon.mapper.OrdersMapper;
import top.hepingan.pacommon.mapper.ProductMapper;
import top.hepingan.pacommon.mapper.UserMapper;
import top.hepingan.pacommon.utils.*;
import top.hepingan.pacommon.vo.AlipayPayCodeVo;
import top.hepingan.pacommon.vo.OrderPageVo;
import top.hepingan.pacommon.vo.ProductVo;
import top.hepingan.pasystemservice.service.PayService;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PayServiceImpl implements PayService {
    private final ProductMapper productMapper;

    private final OrdersMapper ordersMapper;

    private final UserMapper userMapper;


    /**
     * 得到产品列表
     * 产品列表
     *
     * @return {@link List}<{@link ProductVo}>
     */
    @Override
    public List<ProductVo> getProductList() {
        return productMapper.selectList(
                new QueryWrapper<Product>()
                        .lambda()
                        .select(Product::getProductId, Product::getProductPrice, Product::getProductName, Product::getFrequency)
        ).stream().map(c -> new ProductVo().setProductId(c.getProductId()).setProductPrice(c.getProductPrice()).setFrequency(c.getFrequency()).setProductName(c.getProductName())).collect(Collectors.toList());
    }

    /**
     * 得到当前用户订单页面
     *
     * @param pageNum 页面num
     * @return {@link OrderPageVo}
     */
    @Override
    public OrderPageVo getCurrentUserOrderPage(final int pageNum) {
        final Long userId = UserUtils.getCurrentLoginId();
        return getOrdersPage(userId, null, null, pageNum);
    }

    /**
     * 得到用户订单页面
     *
     * @param pageNum 页面num
     * @param status  状态
     * @return {@link OrderPageVo}
     */
    @Override
    public OrderPageVo getUserOrderPage(int pageNum, String prompt, String status) {
        return getOrdersPage(null, prompt, status, pageNum);
    }

    /**
     * 得到订单页面
     *
     * @param userId  用户id
     * @param state   状态
     * @param pageNum 页面num
     * @return {@link OrderPageVo}
     */
    private OrderPageVo getOrdersPage(final Long userId, final String prompt, final String state, final int pageNum) {
        final OrderPageVo orderPageVo = new OrderPageVo();
        final IPage<OrderPageVo.Orders> convert = ordersMapper.selectPage(new Page<>(pageNum, 15), new QueryWrapper<Orders>()
                .lambda()
                .eq(userId != null, Orders::getUserId, userId)
                .eq(StringUtils.notEmpty(state), Orders::getState, state)
                .eq(StringUtils.notEmpty(prompt), Orders::getOrdersId, prompt)
                .select(
                        Orders::getOrdersId,
                        Orders::getPayTime,
                        Orders::getState,
                        Orders::getProductPrice,
                        Orders::getProductName,
                        Orders::getFrequency,
                        Orders::getReasonFailure
                ).orderByDesc(Orders::getCreatedTime)
        ).convert(o -> new OrderPageVo.Orders()
                .setOrdersId(o.getOrdersId())
                .setReasonFailure(o.getReasonFailure())
                .setState(o.getState())
                .setFrequency(o.getFrequency())
                .setPayTime(o.getPayTime())
                .setProductName(o.getProductName())
                .setProductPrice(o.getProductPrice()));
        orderPageVo.setOrders(convert);
        // 查询总金额
        double totalAmount;
        try {
            totalAmount = (Double) ordersMapper.selectObjs(
                    new QueryWrapper<Orders>()
                            .eq(userId != null, "user_id", userId)
                            .eq("state", 1)
                            .select("sum(product_price)")
            ).stream().findFirst().orElseThrow();
        } catch (Exception e) {
            totalAmount = 0;
        }
        // 将字符串转换为Double类型
        return orderPageVo.setTotalAmount(totalAmount);
    }

    /**
     * 删除产品通过id
     *
     * @param id id
     */
    @Override
    public void deleteProductById(final Long id) {
        productMapper.deleteById(id);
    }


    /**
     * 货架上产品
     *
     * @param dto dto
     */
    @Override
    public void shelvesProduct(final ShelvesProductDto dto) {
        productMapper.insert(BeanUtils.copyClassProperTies(dto, Product.class));
    }

    @Override
    public IPage<ProductVo> getProductPageVo(int pageNum, String prompt) {
        return productMapper.selectPage(new Page<>(pageNum, 15), new QueryWrapper<Product>()
                        .lambda()
                        .like(StringUtils.notEmpty(prompt), Product::getProductName, prompt)
                        .select(Product::getProductId, Product::getProductName, Product::getProductPrice, Product::getFrequency, Product::getCreatedTime)
                )
                .convert(c -> new ProductVo().setProductId(c.getProductId()).setProductName(c.getProductName()).setProductPrice(c.getProductPrice()).setFrequency(c.getFrequency()).setCreatedTime(c.getCreatedTime()));
    }


    @Value("${hupijiao.appid}")
    private String AppId;

    @Value("${hupijiao.appsecret}")
    private String AppSecret;

    @Value("${hupijiao.webUrl}")
    private String webUrl;

    /**
     * 虎皮椒支付宝支付
     * @param productId the product id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AlipayPayCodeVo generatePayQrCode(final Long productId) {
        Product product = productMapper.selectOne(new QueryWrapper<Product>().lambda().eq(Product::getProductId, productId));
        if (product == null) {
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
        String formattedNumber = String.format("%.2f", product.getProductPrice());
        options.put("total_fee",formattedNumber);
        // 必填 标题
        options.put("title",product.getProductName());
        // 必填 当前时间戳 调用 刚写的方法 getSecondTimestamp
        options.put("time", getSecondTimestamp(new Date()));
        // 必填 通知回调地址 url 什么含义 我们后台需要知道 用户支付了。
        options.put("notify_url","https://java.hepingan.top/pay/paycallback"); // 只有这个有用
        // 非必填 使用 响应字段中 url 就直接跳到百度了，如果访问，url_qrcode ，不会直接跳转，只有当支付完成后，再次刷新 url_qrcode中的连接，才会跳转。
        options.put("return_url",webUrl+"/#/user_view");
        // 非必填 用户取消支付，跳转的页面   经过测试，没有触发机制，建议不传递
        options.put("callback_url",webUrl+"/#/user_view");
        //plugins 非必填 备注信息
        options.put("plugins","您支付后的IT币（虚拟货币）将只用于Pass Assistant站内消费.");
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
            Orders orders = new Orders()
                    .setOrdersId(randomNumber)
                    .setUserId(UserUtils.getCurrentLoginId())
                    .setCreatedTime(createdTime)
                    .setUpdateTime(createdTime)
                    .setProductId(productId)
                    .setFrequency(product.getFrequency())
                    .setProductName(product.getProductName())
                    .setProductPrice(product.getProductPrice())
                    .setState(0);
            ordersMapper.insert(orders);
            return new AlipayPayCodeVo()
                    .setOrdersId(orders.getOrdersId())
                    .setCreatedTime(orders.getCreatedTime())
                    .setProductName(orders.getProductName())
                    .setProductPrice(orders.getProductPrice())
                    .setPayUrl(payUrl);
        }catch (OrdersException e){
            e.printStackTrace();
            return null;
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


    /**
     * 虎皮椒检查付款状态
     *
     * @param orderNo 订单
     * @return {@link String}
     */
    @Override
    public String paymentStatus(final String orderNo) {
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
            ordersMapper.update(null,new UpdateWrapper<Orders>()
                    .lambda()
                    .eq(Orders::getOrdersId,orderNo)
                    .set(Orders::getState,1)
                    .set(Orders::getPayTime,LocalDateTime.now()));
            Long frequency = userMapper.getFrequencyById(UserUtils.getCurrentLoginId());
            Orders orders = ordersMapper.selectOne(new QueryWrapper<Orders>()
                    .lambda()
                    .eq(Orders::getOrdersId,orderNo)
                    .select(Orders::getProductId));
            Product product = productMapper.selectOne(new QueryWrapper<Product>()
                    .lambda()
                    .eq(Product::getProductId, orders.getProductId())
                    .select(Product::getFrequency));
            //更新用户的次数
            userMapper.update(null,new UpdateWrapper<User>()
                    .lambda()
                    .eq(User::getUserId,UserUtils.getCurrentLoginId())
                    .set(User::getFrequency,frequency+product.getFrequency()));
            return "success";
        }else if (Objects.equals(status, "WP")){
            return "wait";
        }else {
            return "cancel";
        }
    }
}
