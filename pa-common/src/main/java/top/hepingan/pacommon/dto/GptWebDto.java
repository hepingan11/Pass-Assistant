package top.hepingan.pacommon.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.experimental.Accessors;
import top.hepingan.pacommon.model.GptModel;

import java.util.List;

/**
 * 雨纷纷旧故里草木深
 *
 * @author  @github dulaiduwang003
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class GptWebDto {

    @NotEmpty(message = "消息数据不能为空")
    private List<GptModel.Messages> messages;

}
