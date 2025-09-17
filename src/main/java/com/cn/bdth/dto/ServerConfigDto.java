package com.cn.bdth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 雨纷纷旧故里草木深
 *
 * @author  @github dulaiduwang003
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class ServerConfigDto {

    private String openKey;

    private String deepseekKey;

    private String tongyiKey;

    private String claudeKey;

    private String doubaoKey;

    private String glmKey;

    private String linkTopImg;

    @NotNull(message = "第一次登录奖励次数不能为空")
    private Long incentiveFrequency;

    private Long signInFrequency;

    private Long gptFrequency;

    private Long gptTextImageFrequency;

}
