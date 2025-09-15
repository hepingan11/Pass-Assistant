package top.hepingan.pagatewayservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("top.hepingan.pacommon.mapper")
@ComponentScan(basePackages = {"top.hepingan.pagatewayservice", "top.hepingan.pacommon"})
public class PaGatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaGatewayServiceApplication.class, args);
    }

}
