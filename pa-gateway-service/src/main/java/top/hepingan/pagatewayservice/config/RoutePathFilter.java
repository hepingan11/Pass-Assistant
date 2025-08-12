package top.hepingan.pagatewayservice.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import top.hepingan.pacommon.constants.user.AuthConstant;
import top.hepingan.pacommon.exceptions.ExceptionMessages;
import top.hepingan.pacommon.msg.Result;

/**
 * 路由拦截器
 *
 * @author @github dulaiduwang003
 * @version 1.0
 */
@Configuration
public class RoutePathFilter {
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截地址
                .addInclude("/**")    /* 拦截全部path */
                // 开放地址
                .addExclude("/favicon.ico")
                // 鉴权方法：每次访问进入
                .setAuth(obj -> {
                    // 对于OPTIONS预检请求，直接放行
                    if (SaHolder.getRequest().getMethod().equals(HttpMethod.OPTIONS.toString())) {
                        SaRouter.back();
                        return;
                    }
                    // 登录校验 -- 拦截所有路由，并排除/auth/** 用于开放登录
                    SaRouter.match("/**").notMatch("/auth/**")
                            .check(saRouterStaff -> StpUtil.checkLogin());
                    // 权限认证 -- 不同模块, 校验不同权限
                    SaRouter.match("/admin/**").check(r -> StpUtil.checkRole(AuthConstant.ADMIN));
                })
                // 异常处理方法：每次setAuth函数出现异常时进入
                .setError(e ->
                    Result.error(ExceptionMessages.TOKEN_LAPSE, 401)
                ).setBeforeAuth(r -> {
                    SaHolder.getResponse()
                            .setHeader("Access-Control-Allow-Origin", "*")
                            .setHeader("Access-Control-Allow-Methods", "*")
                            .setHeader("Access-Control-Max-Age", "3600")
                            .setHeader("Access-Control-Allow-Headers", "*")
                            .setServer("Zeus");
                    // 确保OPTIONS预检请求总是被放行
                    if (SaHolder.getRequest().getMethod().equals(HttpMethod.OPTIONS.toString())) {
                        SaRouter.back();
                    }
                });
    }
}