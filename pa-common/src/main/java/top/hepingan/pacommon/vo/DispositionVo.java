package top.hepingan.pacommon.vo;

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

    private String openAiUrl;

    private String openKey;

    private String deepseekKey;

    private String doubaoKey;

    private String claudeKey;

    private String glmKey;

    private Long gptTextImageFrequency;

    private Long gptFrequency;

    private Long gptPlusFrequency;

    private String linkTopImg;

    private String sdButton;

    private String dialogueStorage;

    private Long sdImageFrequency;

    private String sdUrl;

    private Long incentiveFrequency;

    private Long signInFrequency;

    private Long videoFrequency;

    private String isHadoop;
}
