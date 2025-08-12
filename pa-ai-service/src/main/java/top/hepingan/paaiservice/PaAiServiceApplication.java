package top.hepingan.paaiservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(basePackages = {"top.hepingan.paaiservice", "top.hepingan.pacommon"},
//        将FastJSON依赖范围设置为runtime
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = top.hepingan.pacommon.config.JsonDataConfig.class))
public class PaAiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaAiServiceApplication.class, args);
    }

}
