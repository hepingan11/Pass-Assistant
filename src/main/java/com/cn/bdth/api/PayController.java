package com.cn.bdth.api;


import com.cn.bdth.exceptions.OrdersException;
import com.cn.bdth.msg.Result;
import com.cn.bdth.service.PayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 交易性接口
 *
 * @author  @github dulaiduwang003
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/pay")
@RequiredArgsConstructor
public class PayController {


    private final PayService payService;


    /**
     * 生成支付宝支付二维码
     *
     * @param productId the product id
     * @return the result
     */
    @PostMapping(value = "/alipay/pay/{productId}", name = "支付宝支付", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result alipayPayQrCode(@PathVariable final Long productId) {
        try {
            return Result.data(payService.generatePayQrCode(productId));
        } catch (OrdersException e) {
            return Result.error(e.getMessage(), e.getCode());
        }
    }

    /**
     * 获取商品列表
     *
     * @return the product list
     */
    @GetMapping(value = "/product/list", name = "获取商品列", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getProductList() {
        return Result.data(payService.getProductList());
    }

    /**
     * 获取我的打赏订单记录
     *
     * @return the product list
     */
    @GetMapping(value = "/orders/page", name = "获取我的打赏订单记录", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result tippingOrdersPages(@RequestParam(defaultValue = "1") final int pageNum) {
        return Result.data(payService.getCurrentUserOrderPage(pageNum));
    }


    /**
     * 支付宝支付状态查询
     *
     * @param orderId the order id
     * @return the result
     */
    @PostMapping(value = "/alipay/status/{orderId}", name = "支付宝支付状态", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result alipayIsSucceed(@PathVariable final String orderId) {
        return Result.data(payService.paymentStatus(orderId));
    }

    @RequestMapping("/paycallback")
    @ResponseBody
    public String abc(HttpServletRequest request){
        // 记得 map 第二个泛型是数组 要取 第一个元素 即[0]
        Map<String, String[]> parameterMap = request.getParameterMap();
        System.out.println("展示 回调的所有结果：");
        // 处理 回调 结果
        parameterMap.keySet().stream().forEach((k) ->{
            System.out.println(k+":"+parameterMap.get(k)[0]);
        });

        System.out.println("展示 回调的所有结果完成");
        System.out.print("\n最终结果是:");

        if("OD".equals(parameterMap.get("status")[0])){
            System.out.println("用户支付成功了");
        }else {
            System.out.println("用户支付不成功");
        }
        return "ok";
    }


}
