package com.cn.bdth.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 雨纷纷旧故里草木深
 *
 * @author  @github dulaiduwang003
 * @version 1.0
 */
@Component
@Data
public class ChatGptDefaultConfig {


    @Value("${config.openKey}")
    private String openKey;

    @Value("${config.deepseekKey}")
    private String deepseekKey;

    @Value("${config.tongyiKey}")
    private String tongyiKey;

    @Value("${config.claudeKey}")
    private String claudeKey;

    @Value("${config.doubaokey}")
    private String doubaoKey;

    @Value("${config.glmKey}")
    private String glmKey;

    @Value("${config.gptTextImageFrequency}")
    private Long gptTextImageFrequency;

    @Value("${config.gptFrequency}")
    private Long gptFrequency;

}
