package top.hepingan.pasystemservice.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import top.hepingan.pacommon.constants.user.AuthConstant;
import top.hepingan.pacommon.dto.EmailCodeDto;
import top.hepingan.pacommon.dto.EmailContentDto;
import top.hepingan.pacommon.dto.EmailLoginDto;
import top.hepingan.pacommon.entity.User;
import top.hepingan.pacommon.exceptions.EmailBackException;
import top.hepingan.pacommon.exceptions.ExceptionMessages;
import top.hepingan.pacommon.exceptions.LoginPasswordException;
import top.hepingan.pacommon.exceptions.RegistrationException;
import top.hepingan.pacommon.mapper.UserMapper;
import top.hepingan.pacommon.utils.RedisUtils;
import top.hepingan.pasystemservice.common.UserInspiritCommon;
import top.hepingan.pasystemservice.service.AuthService;

import java.util.Date;

/**
 * 登录授权业务处理类
 *
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings("all")
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;

    private final UserInspiritCommon userInspiritCommon;

    private final RedisUtils redisUtils;

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;
    private final static String SALT = "HuJiaXin";

    @Value(value = "${spring.mail.username}")
    private String username;

    @Override
    public void emailBack(final EmailCodeDto dto) {
        final String code = dto.getCode();
        final String KEY = AuthConstant.EMAIL_CODE + dto.getEmail();
        final Object value = redisUtils.getValue(KEY);
        if (value != null && value.equals(code)) {
            final Long l = userMapper.selectCount(new QueryWrapper<User>()
                    .lambda().eq(User::getEmail, dto.getEmail())
                    .select(User::getUserId));
            if (l <= 0) {
                throw new EmailBackException(ExceptionMessages.EMAIL_NOT_EXIST);
            }
            userMapper.update(new User()
                    .setPassword(SaSecureUtil.md5BySalt(dto.getPassword(), SALT)), new QueryWrapper<User>().lambda().eq(User::getEmail, dto.getEmail()));
            redisUtils.delKey(KEY);
        } else {
            throw new EmailBackException(ExceptionMessages.CODE_ERR);
        }
    }

    @Override
    public void emailEnroll(final EmailCodeDto dto) {
        final String code = dto.getCode();
        final String KEY = AuthConstant.EMAIL_CODE + dto.getEmail();
        final Object value = redisUtils.getValue(KEY);
        if (value != null && value.equals(code)) {
            final User user = userMapper.selectOne(new QueryWrapper<User>()
                    .lambda().eq(User::getEmail, dto.getEmail())
                    .select(User::getUserId));
            if (user != null) {
                throw new RegistrationException(ExceptionMessages.ACCOUNT_ALREADY_EXISTS_ERR);
            }

            userMapper.insert(new User()
                    .setEmail(dto.getEmail())
                    .setPassword(SaSecureUtil.md5BySalt(dto.getPassword(), SALT))
                    .setFrequency(userInspiritCommon.getInspiritStructure().getIncentiveFrequency()));
            redisUtils.delKey(KEY);
        } else {
            throw new RegistrationException(ExceptionMessages.CODE_ERR);
        }

    }

    @Override
    public void getEmailEnrollCode(final String email) {
        final String code = RandomStringUtils.random(6, true, true).toUpperCase();
        Context context = new Context();
        context.setVariable("code", code);
        String process = templateEngine.process("emailCode.html", context);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
//            Properties props = System.getProperties();
//            props.put("mail.smtp.auth", "true");
//            props.put("mail.smtp.starttls.enable", "true");
//            props.put("mail.smtp.host", "smtp.qq.com");
//            props.put("mail.smtp.port", "587");
            helper.setSubject("IT乌托邦-验证码");
            helper.setFrom(username);
            helper.setTo(email);
            helper.setSentDate(new Date());
            helper.setText(process, true);
            redisUtils.setValueTimeout(AuthConstant.EMAIL_CODE + email, code, 300);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RegistrationException(ExceptionMessages.VERIFICATION_CODE_ERR, 500);
        }
    }

    @Override
    public String emailLogin(final EmailLoginDto dto) {
        final User user = userMapper.selectOne(new QueryWrapper<User>()
                .lambda()
                .eq(User::getEmail, dto.getEmail())
                .eq(User::getPassword, SaSecureUtil.md5BySalt(dto.getPassword(), SALT))
                .select(User::getType, User::getUserId)

        );
        if (user == null) {
            throw new LoginPasswordException(ExceptionMessages.EMAIL_LOGIN_PWD_ERR);
        }
        StpUtil.login(user.getUserId());
        //设置具体TOKEN Session权限
        StpUtil.getSession()
                .set(AuthConstant.ROLE, user.getType());
        return StpUtil.getTokenValue();
    }


    @Override
    public void logout() {
        if (StpUtil.isLogin()) {
            StpUtil.logout();
        }
    }

    @Override
    public void submitEmailContent(EmailContentDto emailContentDto) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = null; // true 表示支持 HTML
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(username); // 发件人邮箱
            helper.setTo("1973016127@qq.com"); // 收件人邮箱
            helper.setSubject("IT乌托邦-网站来信"); // 邮件主题
            String Content = "<h1>称呼："+emailContentDto.getName()+"</h1><p>"+emailContentDto.getContent()+"<br/> <strong>联系方式："+emailContentDto.getEmail()+"</strong> </p>";
            helper.setText(Content,true); // 邮件内容
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RegistrationException(ExceptionMessages.VERIFICATION_CODE_ERR, 500);
        }
    }


}
