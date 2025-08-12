package top.hepingan.pasystemservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@MapperScan("top.hepingan.pacommon.mapper")
@ComponentScan(basePackages = {"top.hepingan.pasystemservice", "top.hepingan.pacommon"})
@EnableFeignClients(basePackages = "top.hepingan.pasystemservice.feignClient")
public class PaSystemServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaSystemServiceApplication.class, args);
    }

}
