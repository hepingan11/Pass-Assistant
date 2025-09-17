package com.cn.bdth.exceptions;

/**
 * 异常消息
 *
 * @author  @github dulaiduwang003
 * @version 1.0
 */
public interface ExceptionMessages {

    String EMAIL_LOGIN_PWD_ERR = "邮箱或密码错误";

    String ACCOUNT_ALREADY_EXISTS_ERR = "该账号已存在";

    String EMAIL_NOT_EXIST = "该邮箱不存在";
    String CODE_ERR = "验证码错误";
    String NOT_EXIST_CODE = "兑换码不存在或已失效";

    String VERIFICATION_CODE_ERR = "生成验证码失败";

    String NOT_PAID_FOR_LONG_TIME = "长时间未支付,已自动取消";

    String PRODUCT_NULL_ERR = "商品不存在";

    String CONCURRENT = "手速太快了! 请稍后重新点击";

    String IS_SIGN_IN = "今天你已经签到过了,请明天再来";


    String GPT_TIMEOUT = "哦豁!现在貌似出了点问题 请重新发送试试";

    String GPT_CONFIG_ERR = "哦豁!貌似参数配置不对 请检查一下Ai配置";


    String GPT_ERR = "建立连接失败,请重新发送数据";

    String TOKEN_LAPSE = "登录信息已过期,请重新登录";

}
