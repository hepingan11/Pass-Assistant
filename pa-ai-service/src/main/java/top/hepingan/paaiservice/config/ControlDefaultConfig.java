package top.hepingan.paaiservice.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 雨纷纷旧故里草木深
 *
 * @author  @github dulaiduwang003
 * @version 1.0
 */

@Data
@Component
public class ControlDefaultConfig {


    @Value("${control.proxyIp}")
    private String proxyIp;

    @Value("${control.proxyPort}")
    private Integer proxyPort;

    @Value("${control.sensitiveWords}")
    private String sensitiveWords;

    @Value("${control.enableSensitive}")
    private Boolean enableSensitive;

    @Value("${control.enableGptPlus}")
    private Boolean enableGptPlus;

    @Value("${control.enableProxy}")
    private Boolean enableProxy;


}
