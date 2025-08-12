package top.hepingan.patoolsservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("top.hepingan.pacommon.mapper")
@ComponentScan(basePackages = {"top.hepingan.patoolsservice", "top.hepingan.pacommon"})
public class PaToolsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaToolsServiceApplication.class, args);
    }

}
