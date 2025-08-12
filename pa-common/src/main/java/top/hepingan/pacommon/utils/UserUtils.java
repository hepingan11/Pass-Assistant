package top.hepingan.pacommon.utils;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import top.hepingan.pacommon.constants.user.AuthConstant;


/**
 * 用户工具类
 *
 * @author  @github dulaiduwang003
 * @version 1.0
 */
public class UserUtils {


    public static Long getLoginIdByToken(final String token) {
        final Object loginIdObject = StpUtil.getLoginIdByToken(token);
        return Long.valueOf(String.valueOf(loginIdObject));
    }

    public static Long getCurrentLoginId() {
        return Long.valueOf(String.valueOf(StpUtil.getLoginId()));
    }


    public static String getCurrentRole() {
        final SaSession session = StpUtil.getSession();
        return String.valueOf(session.get(AuthConstant.ROLE));
    }

}
