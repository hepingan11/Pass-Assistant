package top.hepingan.pasystemservice.config;

import cn.dev33.satoken.stp.StpInterface;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.hepingan.pacommon.entity.User;
import top.hepingan.pacommon.mapper.UserMapper;
import top.hepingan.pacommon.utils.UserUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 权限拦截处理类
 * @author  @github dulaiduwang003
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class AuthInterfaceFilter implements StpInterface {


    private final UserMapper userMapper;

    /**
     * 获取当前用户拥有的权限
     *
     * @param o the o
     * @param s the s
     * @return the role list
     */
    @Override
    public List<String> getRoleList(Object o, String s) {
        // 本 list 仅做模拟，实际项目中要根据具体业务逻辑来查询角色
        List<String> list = new ArrayList<>();
        User user = userMapper.selectById(UserUtils.getCurrentLoginId());
        if (Objects.equals(user.getType(), "ADMIN")){
            list.add("ADMIN");
        }
        return list;
    }

    /**
     * 获取当前用户拥有的权限
     *
     * @param o the o
     * @param s the s
     * @return the permission list
     */
    @Override
    public List<String> getPermissionList(Object o, String s) {
        return null;
    }


}
