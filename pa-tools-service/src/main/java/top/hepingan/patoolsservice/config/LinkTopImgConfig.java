package top.hepingan.patoolsservice.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class LinkTopImgConfig {

    @Value("${config.linkTopImg}")
    private String linkTopImg;
}