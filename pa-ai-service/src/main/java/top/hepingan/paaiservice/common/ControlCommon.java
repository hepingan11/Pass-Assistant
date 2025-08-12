package top.hepingan.paaiservice.common;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.hepingan.paaiservice.config.ControlDefaultConfig;
import top.hepingan.pacommon.constants.ServerConstant;
import top.hepingan.pacommon.structure.ControlStructure;
import top.hepingan.pacommon.utils.RedisUtils;

/**
 * 雨纷纷旧故里草木深
 *
 * @author  @github dulaiduwang003
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ControlCommon {


    private final RedisUtils redisUtils;

    private final ControlDefaultConfig controlDefaultConfig;


    public ControlStructure getControl() {
        try {
            final ControlStructure value = (ControlStructure) redisUtils.getValue(ServerConstant.TERMINAL_CONFIG);
            if (value == null) {
                log.warn("当前正在使用服务器默认终端配置,请及时前往控制台配置服务器终端参数");
                return createdDefaultServer();
            } else {
                return value;
            }
        } catch (Exception e) {
            redisUtils.delKey(ServerConstant.TERMINAL_CONFIG);
            log.warn("已清除旧版本的服务器配置,当前正在使用服务器默认配置,请及时前往控制台配置服务器参数");
            return createdDefaultServer();

        }
    }

    private ControlStructure createdDefaultServer() {
        log.info(controlDefaultConfig.toString());
        return new ControlStructure()
                .setEnableGptPlus(controlDefaultConfig.getEnableGptPlus())
                .setProxyIp(controlDefaultConfig.getProxyIp())
                .setProxyPort(controlDefaultConfig.getProxyPort())
                .setEnableProxy(controlDefaultConfig.getEnableProxy())
                .setSensitiveWords(controlDefaultConfig.getSensitiveWords())
                .setEnableSensitive(controlDefaultConfig.getEnableSensitive());

    }

}
