package top.hepingan.pasystemservice.service;


import top.hepingan.pacommon.dto.EmailCodeDto;
import top.hepingan.pacommon.dto.EmailContentDto;
import top.hepingan.pacommon.dto.EmailLoginDto;

/**
 * 登录接口
 *
 * @author 时间海 @github dulaiduwang003
 * @version 1.0
 */
public interface AuthService {


    /**
     * 邮箱注册
     */
    void emailEnroll(final EmailCodeDto dto);


    /**
     * 找回密码
     */
    void emailBack(final EmailCodeDto dto);


    /**
     * 获取邮箱二维码
     */
    void getEmailEnrollCode(final String email);

    /**
     * 邮箱登录
     */
    String emailLogin(final EmailLoginDto dto);


    /**
     * 注销
     */
    void logout();

    void submitEmailContent(EmailContentDto emailContentDto);
}
