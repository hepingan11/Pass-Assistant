package com.cn.bdth.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 雨纷纷旧故里草木深
 *
 * @author  @github dulaiduwang003
 * @version 1.0
 */
@Data
public class DispositionVo implements Serializable {

    private String openKey;

    private String deepseekKey;

    private String claudeKey;

    private String glmKey;

    private String tongyiKey;

    private String doubaoKey;

    private Long gptTextImageFrequency;

    private Long gptFrequency;

    private String linkTopImg;

    private Long incentiveFrequency;

    private Long signInFrequency;
}
