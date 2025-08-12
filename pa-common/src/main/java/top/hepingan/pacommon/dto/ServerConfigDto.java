package top.hepingan.pacommon.dto;

import jakarta.validation.constraints.NotBlank;
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

    private String sdUrl;

    private String openAiUrl;

    private String sdButton;

    @NotBlank(message = "是否开启SD绘画不能为空")
    private String dialogueStorage;

    private String openKey;

    private String deepseekKey;

    private String doubaoKey;

    private String claudeKey;

    private String glmKey;

    private String linkTopImg;

    private String isHadoop;

    @NotNull(message = "第一次登录奖励次数不能为空")
    private Long incentiveFrequency;

    private Long videoFrequency;

    private Long signInFrequency;

    private Long sdImageFrequency;

    private Long gptFrequency;

    private Long gptTextImageFrequency;

}
