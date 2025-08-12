package top.hepingan.pacommon.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 雨纷纷旧故里草木深
 *
 * @author  @github dulaiduwang003
 * @version 1.0
 */
@Data
public class StarDialogueDto {

    private Long starId;

    @NotBlank(message = "问题不能为空")
    private String issue;

    @NotBlank(message = "回答不能为空")
    private String answer;


}
