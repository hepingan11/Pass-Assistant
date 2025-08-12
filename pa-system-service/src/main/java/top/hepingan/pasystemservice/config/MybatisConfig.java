package top.hepingan.pasystemservice.config;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;


/**
 * MyBatisPlus插件配置
 *
 * @author  @github dulaiduwang003
 * @version 1.0
 */

@Configuration
@MapperScan("top.hepingan.pacommon.mapper")
public class MybatisConfig {


}
